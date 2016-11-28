package com.firstwap.dispatcher.observer.utility;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.firstwap.dispatcher.observer.controller.NotificationController;

@Component
public class ApplicationContextAccessor implements ApplicationContextAware {

	private static ApplicationContext context;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		context = applicationContext;
	}

	public static ApplicationContext getApplicationContext() {
		return context;
	}

	public static void updateErrorMessage(String message) {
		getApplicationContext().getBean(NotificationController.class).ERROR_MESSAGE = message;
	}

	public static void resetErrorMessage() {
		getApplicationContext().getBean(NotificationController.class).ERROR_MESSAGE = "";
	}

}
