package com.softlerda.pugfriendi.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.softlerda.pugfriendi.entity.Account;
import com.softlerda.pugfriendi.entity.Role;
import com.softlerda.pugfriendi.service.IAccountService;
import com.softlerda.pugfriendi.util.MenssageApi;

@CrossOrigin(origins = { "http://localhost:4200" })
@RestController
@RequestMapping("/api")
public class AccountController {

	@Autowired
	private IAccountService accountService;

	@PostMapping("/accounts")
	public ResponseEntity<?> newAccount(@RequestBody Account newAccount) {

		if (newAccount == null) {
			return new ResponseEntity<MenssageApi>(new MenssageApi("Formato de petición incorrecto."),
					HttpStatus.CONFLICT);
		}

		if (this.accountService.findByEmail(newAccount.getEmail()) != null) {
			return new ResponseEntity<MenssageApi>(
					new MenssageApi("El email " + newAccount.getEmail() + " ya esta assignado a otro usuario."),
					HttpStatus.CONFLICT);
		}

		if (this.accountService.findByUsername(newAccount.getUsername()) != null) {
			return new ResponseEntity<MenssageApi>(
					new MenssageApi("El usuario " + newAccount.getUsername() + " ya existe"), HttpStatus.CONFLICT);
		}

		// Habilita el usuario
		newAccount.setEnabled(true);

		// Define los roles del usuario
		List<Role> roles = new ArrayList<>();
		Role role = new Role();
		role.setAuthority("ROLE_USER");
		roles.add(role);
		newAccount.setRoles(roles);

		// Guarda la contraseña encriptada
		newAccount.setPassword(new BCryptPasswordEncoder().encode(newAccount.getPassword()));

		// Guarda el usuario
		this.accountService.saveAccount(newAccount);

		return new ResponseEntity<Account>(newAccount, HttpStatus.OK);
	}

	public static final String ACCOUNT_UPLOADED_FOLDER = "uploads/accounts/";

	// CREATE TEACHER IMAGE
	@RequestMapping(value = "/accounts/images", method = RequestMethod.POST, headers = ("content-type=multipart/form-data"))
	public ResponseEntity<?> uploadTeacherImage(@RequestParam("id_account") Long idAccount,
			@RequestParam("file") MultipartFile multipartFile, UriComponentsBuilder componentsBuilder) {

		if (idAccount == null) {
			return new ResponseEntity<MenssageApi>(new MenssageApi("Introduzca una cuenta"), HttpStatus.NO_CONTENT);
		}

		if (multipartFile.isEmpty()) {
			return new ResponseEntity<MenssageApi>(new MenssageApi("Seleccione un fichero"), HttpStatus.NO_CONTENT);
		}

		Account account = this.accountService.findById(idAccount);
		if (account == null) {
			return new ResponseEntity<MenssageApi>(new MenssageApi("Esta cuenta no exite"), HttpStatus.NOT_FOUND);
		}

		if (!account.getImage().isEmpty() || account.getImage() != null) {
			String fileName = account.getImage();
			Path path = Paths.get(fileName);
			File f = path.toFile();
			if (f.exists()) {
				f.delete();
			}
		}

		try {
			Date date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			String dateName = dateFormat.format(date);

			String fileName = String.valueOf(idAccount) + "-imageAccount-" + dateName + "."
					+ multipartFile.getContentType().split("/")[1];
			account.setImage(ACCOUNT_UPLOADED_FOLDER + fileName);

			byte[] bytes = multipartFile.getBytes();
			Path path = Paths.get(ACCOUNT_UPLOADED_FOLDER + fileName);
			Files.write(path, bytes);

			this.accountService.saveAccount(account);
			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(bytes);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return new ResponseEntity<MenssageApi>(
					new MenssageApi("Error durante la subida: " + multipartFile.getOriginalFilename()),
					HttpStatus.CONFLICT);
		}
	}

}
