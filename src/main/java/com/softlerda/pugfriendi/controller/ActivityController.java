package com.softlerda.pugfriendi.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.softlerda.pugfriendi.entity.Activity;
import com.softlerda.pugfriendi.service.IActivityService;
import com.softlerda.pugfriendi.util.CustomErrorType;

@CrossOrigin(origins = { "http://localhost:4200" })
@RestController
@RequestMapping("/api")
public class ActivityController {

	@Autowired
	private IActivityService activityService;

	@GetMapping("/activities")
	public List<Activity> index() {
		return activityService.findAllByOrderByCreationDateDesc();
	}

	@GetMapping("/activities/{id}")
	public Activity show(@PathVariable Long id) {
		return this.activityService.findActivityById(id);
	}

	@PostMapping("/activities")
	@ResponseStatus(HttpStatus.CREATED)
	public Activity create(@RequestBody Activity activity) {

		// Intrduce la fecha de creacion de la actividad
		activity.setCreationDate(new Date());

		this.activityService.saveActivity(activity);
		return activity;
	}

	@PutMapping("/activities/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public Activity update(@RequestBody Activity activity, @PathVariable Long id) {
		Activity currentActivity = this.activityService.findActivityById(id);

		currentActivity.setTitle(activity.getTitle());
		currentActivity.setActivityDate(activity.getActivityDate());
		currentActivity.setStartTime(activity.getStartTime());
		currentActivity.setEndTime(activity.getEndTime());
		currentActivity.setDescription(activity.getDescription());
		currentActivity.setPlaceActivity(activity.getPlaceActivity());
		currentActivity.setMeetingPoint(activity.getMeetingPoint());
		currentActivity.setImageEvent(activity.getImageEvent());
		this.activityService.saveActivity(currentActivity);
		return currentActivity;
	}

	@DeleteMapping("/activities/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		Activity currentActivity = this.activityService.findActivityById(id);
		this.activityService.deleteActivity(currentActivity);
	}

	public static final String ACTIVITY_UPLOADED_FOLDER = "images/activities/";

	// CREATE TEACHER IMAGE
	@PostMapping("/activities/images")
	public ResponseEntity<byte[]> uploadActivityImage(@RequestParam("id_activity") Long idActivity,
			@RequestParam("file") MultipartFile multipartFile) {

		if (idActivity == null) {
			return new ResponseEntity(new CustomErrorType("Please set id_activity"), HttpStatus.NO_CONTENT);
		}

		if (multipartFile.isEmpty()) {
			return new ResponseEntity(new CustomErrorType("Please select a file to upload"), HttpStatus.NO_CONTENT);
		}

		Activity activity = this.activityService.findActivityById(idActivity);
		if (activity == null) {
			return new ResponseEntity(new CustomErrorType("Activity with id_activity: " + idActivity + " not dfound"),
					HttpStatus.NOT_FOUND);
		}

		if (activity.getImageEvent() != null) {
			if (!activity.getImageEvent().isEmpty()) {
				String fileName = activity.getImageEvent();
				Path path = Paths.get(fileName);
				File f = path.toFile();
				if (f.exists()) {
					f.delete();
				}
			}

		}

		try {
			Date date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			String dateName = dateFormat.format(date);

			String fileName = String.valueOf(idActivity) + "-imageActivity-" + dateName + "."
					+ multipartFile.getContentType().split("/")[1];
			activity.setImageEvent(ACTIVITY_UPLOADED_FOLDER + fileName);

			byte[] bytes = multipartFile.getBytes();
			Path path = Paths.get(ACTIVITY_UPLOADED_FOLDER + fileName);
			Files.write(path, bytes);

			this.activityService.saveActivity(activity);
			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(bytes);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return new ResponseEntity(
					new CustomErrorType("Error during upload: " + multipartFile.getOriginalFilename()),
					HttpStatus.CONFLICT);
		}
	}

	// GET IMAGE
	@RequestMapping(value = "/activities/{id_activity}/images", method = RequestMethod.GET)
	public ResponseEntity<byte[]> getActivityImage(@PathVariable("id_activity") Long idActivity) {
		if (idActivity == null) {
			return new ResponseEntity(new CustomErrorType("Please set id_activity "), HttpStatus.NO_CONTENT);
		}

		Activity activity = this.activityService.findActivityById(idActivity);
		if (activity == null) {
			return new ResponseEntity(new CustomErrorType("Activity with id_activity: " + idActivity + " not found"),
					HttpStatus.NOT_FOUND);
		}

		try {

			String fileName = activity.getImageEvent();
			Path path = Paths.get(fileName);
			File f = path.toFile();
			if (!f.exists()) {
				return new ResponseEntity(new CustomErrorType("Image not found"), HttpStatus.CONFLICT);
			}

			byte[] image = Files.readAllBytes(path);
			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return new ResponseEntity(new CustomErrorType("Error to show image"), HttpStatus.CONFLICT);
		}

	}

	@RequestMapping(value = "/activities/{id_activity}/images", method = RequestMethod.DELETE, headers = "Accept=application/json")
	public ResponseEntity<?> deleteActivityImage(@PathVariable("id_activity") Long idActivity) {
		if (idActivity == null) {
			return new ResponseEntity(new CustomErrorType("Please set id_activity "), HttpStatus.NO_CONTENT);
		}

		Activity activity = this.activityService.findActivityById(idActivity);
		if (activity == null) {
			return new ResponseEntity(new CustomErrorType("Activity with id_activity: " + idActivity + " not found"),
					HttpStatus.NOT_FOUND);
		}

		if (activity.getImageEvent().isEmpty() || activity.getImageEvent() == null) {
			return new ResponseEntity(new CustomErrorType("This Activity dosen't have image assigned"),
					HttpStatus.NO_CONTENT);
		}

		String fileName = activity.getImageEvent();
		Path path = Paths.get(fileName);
		File file = path.toFile();
		if (file.exists()) {
			file.delete();
		}

		activity.setImageEvent("");
		this.activityService.saveActivity(activity);

		return new ResponseEntity<Activity>(HttpStatus.NO_CONTENT);
	}

	@PostMapping("/post")
	public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
		String message = "";
		try {

			message = "You successfully uploaded " + file.getOriginalFilename() + "!";
			return ResponseEntity.status(HttpStatus.OK).body(message);
		} catch (Exception e) {
			message = "FAIL to upload " + file.getOriginalFilename() + "!";
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
		}
	}

}
