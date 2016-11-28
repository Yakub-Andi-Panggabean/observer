package com.firstwap.dispatcher.observer.controller;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.firstwap.dispatcher.observer.utility.GenericResponse;

@RestController
@RequestMapping(value = NotificationController.ROOT_PATH)
public class NotificationController {

	private static final Logger log = LoggerFactory
			.getLogger(NotificationController.class);

	public static final String ROOT_PATH = "/notification";
	public static final String ERROR = "/error";

	public volatile String ERROR_MESSAGE = "";

	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = ERROR, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public GenericResponse errorNotification() {
		try {
			while (true) {
				TimeUnit.SECONDS.sleep(6);
				if (!ERROR_MESSAGE.equals("")) {
					log.debug(ERROR_MESSAGE);
					return new GenericResponse("1", ERROR_MESSAGE);
				} else {
					continue;
				}
			}
		} catch (Exception exception) {
			return new GenericResponse("-1", exception.getMessage());
		}
	}
}
