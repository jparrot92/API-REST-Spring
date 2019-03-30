package com.softlerda.pugfriendi.service;

import java.util.List;

import com.softlerda.pugfriendi.entity.Activity;

public interface IActivityService {

	public List<Activity> findAllByOrderByCreationDateDesc();
	
	public void saveActivity(Activity activity);
	
	public Activity findActivityById(Long id);
	
	public void deleteActivity(Activity activity);

}
