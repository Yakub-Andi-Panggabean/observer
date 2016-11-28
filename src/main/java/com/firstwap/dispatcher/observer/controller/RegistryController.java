package com.firstwap.dispatcher.observer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.firstwap.dispatcher.observer.domain.Application;
import com.firstwap.dispatcher.observer.service.ProcessRegistryService;
import com.firstwap.dispatcher.observer.utility.GenericResponse;

/**
 *
 * @author yakub
 *
 * @createdOn Sep 15, 2016 10:06:41 AM
 *
 */

@RestController
@RequestMapping(value = RegistryController.ROOT)
public class RegistryController {

	public static final String ROOT = "/monitor/registry";

	@Autowired
	private ProcessRegistryService service;

	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public GenericResponse addNewRegistry(@RequestBody Application application) {
		GenericResponse response = new GenericResponse();
		try {
			service.addNewRegistry(application);
			response.setCode("1");
			response.setDescription("added successfully");
		} catch (Exception exception) {
			response.setCode("-1");
			response.setDescription(exception.getMessage());
		}
		return response;

	}

	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public GenericResponse updateRegistry(@RequestBody Application application) {
		GenericResponse response = new GenericResponse();
		try {
			service.updateRegistry(application);
			response.setCode("1");
			response.setDescription("updated successfully");
		} catch (Exception exception) {
			response.setCode("-1");
			response.setDescription(exception.getMessage());
		}
		return response;

	}

	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public GenericResponse deleteRegistry(@RequestBody Application application) {
		GenericResponse response = new GenericResponse();
		try {
			service.removeRegistry(application);
			response.setCode("1");
			response.setDescription("deleted successfully");
		} catch (Exception exception) {
			response.setCode("-1");
			response.setDescription(exception.getMessage());
		}
		return response;
	}
}
