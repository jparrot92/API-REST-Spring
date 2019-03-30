package com.softlerda.pugfriendi.entity;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public class MyUserDetails extends org.springframework.security.core.userdetails.User {


	private static final long serialVersionUID = -5043463492291017415L;
	
	private Account account;

	public MyUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities,
			Account account) {
		super(username, password, authorities);

		this.account = account;
		// TODO Auto-generated constructor stub
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

}