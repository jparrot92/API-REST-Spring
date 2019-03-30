package com.softlerda.pugfriendi.auth.service;

import java.io.IOException;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import io.jsonwebtoken.Claims;

public interface JWTService {
	
	/**
	 * Método que sé encargar de crear el token.
	 * 
	 * @param auth 
	 * @return
	 * @throws IOException
	 */
	public String create(Authentication auth) throws IOException;
	
	/**
	 * Metodo que valida que el token que biene del HEADER sea correcto.
	 * 
	 * @param token
	 * @return
	 */
	public boolean validate(String token);
	
	/**
	 * Método para obtener los "Claims".
	 * 
	 * @param token
	 * @return
	 */
	public Claims getClaims(String token);
	
	/**
	 * Método que obtine el nombre del usuario des del token.
	 * 
	 * @param token
	 * @return
	 */
	public String getUsername(String token);
	
	/**
	 * Método para obtiner los roles des del token.
	 * 
	 * @param token
	 * @return
	 * @throws IOException
	 */
	public Collection<? extends GrantedAuthority> getRoles(String token) throws IOException;
	
	/**
	 * Método que eliminar el "Bearer ", y entraga solo el token.
	 * 
	 * @param token
	 * @return
	 */
	public String resolve(String token);
}
