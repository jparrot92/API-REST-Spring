package com.softlerda.pugfriendi.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.softlerda.pugfriendi.dao.IAccountDao;
import com.softlerda.pugfriendi.entity.Account;
import com.softlerda.pugfriendi.service.IAccountService;

@Service
@Transactional
public class AccountServiceImpl implements IAccountService {

	@Autowired
	private IAccountDao accountDao;

	@Override
	public Account findByUsername(String username) {
		return accountDao.findByUsername(username);
	}

	@Override
	public Account findByEmail(String email) {
		return accountDao.findByEmail(email);
	}
	
	@Override
	public void saveAccount(Account account) {
		accountDao.save(account);
	}

	@Override
	public Account findById(Long id) {
		// TODO Auto-generated method stub
		return accountDao.findById(id).orElse(null);
	}

}
