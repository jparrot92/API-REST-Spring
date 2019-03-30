package com.softlerda.pugfriendi.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.softlerda.pugfriendi.dao.IActivityDao;
import com.softlerda.pugfriendi.entity.Activity;
import com.softlerda.pugfriendi.service.IActivityService;

@Service
@Transactional
public class ActivityServiceImpl implements IActivityService {

	@Autowired
	private IActivityDao activityDao;

	@Override
	public List<Activity> findAllByOrderByCreationDateDesc() {
		return (List<Activity>) activityDao.findAllByOrderByCreationDateDesc();
	}

	@Override
	public void saveActivity(Activity activity) {
		activityDao.save(activity);
	}

	@Override
	public Activity findActivityById(Long id) {
		return activityDao.findById(id).orElse(null);
	}

	@Override
	public void deleteActivity(Activity activity) {
		activityDao.delete(activity);
	}



}
