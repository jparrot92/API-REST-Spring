package com.softlerda.pugfriendi.auth.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.softlerda.pugfriendi.auth.service.JWTService;
import com.softlerda.pugfriendi.auth.service.JWTServiceImpl;


/**
 * Valida que el token generado sea correcto. Todas la peticiones, a excepción del "/login", pasaran por este filtro.
 *
 */
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
	
	private JWTService jwtService;

	public JWTAuthorizationFilter(AuthenticationManager authenticationManager, JWTService jwtService) {
		super(authenticationManager);
		this.jwtService = jwtService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		// Obtiene el token que viene en la cabezera de la peticion
		String token = request.getHeader(JWTServiceImpl.HEADER_STRING);
		
		// Valida el token
		if (!requiresAuthentication(token)) {
			chain.doFilter(request, response);
			return;
		}

		UsernamePasswordAuthenticationToken authentication = null;
		
		if(jwtService.validate(token)) {
			authentication = new UsernamePasswordAuthenticationToken(jwtService.getUsername(token), null, jwtService.getRoles(token));
		}
		
		// Autentica el usuario, y continua con la cadena de ejecución.
		SecurityContextHolder.getContext().setAuthentication(authentication);
		chain.doFilter(request, response);
		
	}
	
	/**
	 * Método que valida si token, que viene en la cabecera de la petición, no este vació e impiza con "Bearer".
	 * 
	 */
	protected boolean requiresAuthentication(String token) {

		if (token == null || !token.startsWith(JWTServiceImpl.TOKEN_PREFIX)) {
			return false;
		}
		return true;
	}

}
