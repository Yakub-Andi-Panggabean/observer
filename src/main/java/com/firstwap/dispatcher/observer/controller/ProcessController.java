package com.firstwap.dispatcher.observer.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.firstwap.dispatcher.observer.domain.ProcessDomain;
import com.firstwap.dispatcher.observer.service.ProcessService;
import com.firstwap.dispatcher.observer.utility.GenericMultipleResponses;
import com.firstwap.dispatcher.observer.utility.GenericResponse;

/**
 *
 * @author yakub
 *
 * @createdOn Sep 6, 2016 10:01:26 AM
 *
 */

@RestController
@RequestMapping(value = ProcessController.ROOT_PATH)
public class ProcessController {

	public static final String ROOT_PATH = "/monitoring/process";
	private static final String ALL = "/all";

	@Autowired
	private ProcessService service;

	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public GenericMultipleResponses<ProcessDomain> getAllProcessDomain() {
		GenericMultipleResponses<ProcessDomain> responses = new GenericMultipleResponses<>();
		try {
			responses.setContents(service.fetchListProcess());
			responses.setCode("1");
			responses.setDescription("success");
			responses.setTotal(String
					.valueOf(service.fetchListProcess().size()));
		} catch (Exception exception) {
			exception.printStackTrace();
			responses.setContents(null);
			responses.setCode("0");
			responses.setDescription("failed : " + exception.getMessage());
			responses.setTotal("0");
		}
		return responses;
	}

	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public GenericResponse killProcess(
			@RequestBody MultiValueMap<String, String> formData) {
		String pid = formData.getFirst("pid");
		String stopScript = formData.get("stopScript").get(0);
		GenericResponse response = new GenericResponse();
		try {
			service.killProcess(pid, stopScript);
			response.setCode("1");
			response.setDescription("success");
		} catch (Exception exception) {
			response.setCode("-1");
			response.setDescription(exception.getMessage());
		}
		return response;
	}

	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public GenericResponse startProcess(
			@RequestBody MultiValueMap<String, String> formData) {
		GenericResponse response = new GenericResponse();
		try {
			String request = formData.getFirst("location");
			service.startProcess(request);
			response.setCode("1");
			response.setDescription("success");
		} catch (Exception exception) {
			response.setCode("-1");
			response.setDescription(exception.getMessage());
		}
		return response;
	}

	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = ALL, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public GenericResponse startAllProcess() {
		GenericResponse response = new GenericResponse();
		try {
			List<ProcessDomain> list = service.fetchListProcess();
			service.startAllProcess(list);
			response.setCode("1");
			response.setDescription("success");
		} catch (Exception exception) {
			response.setCode("-1");
			response.setDescription(exception.getMessage());
		}
		return response;
	}

	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = ALL, method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public GenericResponse killAllProcess() {
		GenericResponse response = new GenericResponse();
		try {
			service.killAllProcess(service.fetchListProcess());
			response.setCode("1");
			response.setDescription("success");
		} catch (Exception exception) {
			response.setCode("-1");
			response.setDescription(exception.getMessage());
		}
		return response;
	}

}
