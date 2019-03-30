package com.softlerda.pugfriendi.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.softlerda.pugfriendi.entity.Activity;

public interface IActivityDao extends CrudRepository<Activity, Long>{
	public List<Activity> findAllByOrderByCreationDateDesc();
}
