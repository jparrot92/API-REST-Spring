package com.softlerda.pugfriendi.dao;

import org.springframework.data.repository.CrudRepository;

import com.softlerda.pugfriendi.entity.Account;

public interface IAccountDao extends CrudRepository<Account, Long>{

	public Account findByUsername(String username);
	
	public Account findByEmail(String email);
	
}
