package com.softlerda.pugfriendi.service;

import com.softlerda.pugfriendi.entity.Account;

public interface IAccountService {
	
	public Account findByUsername(String username);
	
	public Account findByEmail(String email);
	
	public Account findById(Long id);
	
	public void saveAccount(Account account);

}
