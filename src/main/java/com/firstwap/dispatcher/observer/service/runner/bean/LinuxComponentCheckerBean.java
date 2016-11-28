package com.firstwap.dispatcher.observer.service.runner.bean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firstwap.dispatcher.observer.domain.Application;
import com.firstwap.dispatcher.observer.domain.Container;
import com.firstwap.dispatcher.observer.repository.LinuxProcessRepository;
import com.firstwap.dispatcher.observer.service.runner.ComponentChecker;
import com.firstwap.dispatcher.observer.utility.ObserverException;

/**
 *
 * @author yakub
 *
 * @createdOn Sep 2, 2016 1:37:14 PM
 *
 */

@Component("linux")
public class LinuxComponentCheckerBean implements ComponentChecker {

	private static final Logger LOG = LoggerFactory
			.getLogger(ComponentChecker.class);

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private LinuxProcessRepository repository;

	@Value("${applications_list}")
	private String applications;

	@Override
	public synchronized void checkProcesses() throws ObserverException {
		// TODO Auto-generated method stub

		LOG.debug("application file  : {}", applications);

		File file = new File(applications);
		if (!file.exists()) {
			throw new RuntimeException("application file is not exist");
		}

		try {
			Container container = mapper.readValue(file, Container.class);

			LOG.debug("json content: {}", container.toString());

			for (Application app : container.getApplications()) {

				// LOG.info("application : {}", app.toString());

				if (!isProcessRunning(app)) {
					LOG.debug("start script location : {}",
							app.getStartScript());

					if (!isProcessRunning(app) && app.isAutomatic()) {
						processingApps(app);
					} else {
						LOG.debug(
								"{} service is not running and not in automatic restart mode",
								app.getName());
					}
				}
			}
		} catch (Exception ex) {
			throw new ObserverException(ex.getMessage());
		}

	}

	@Override
	public void processingApps(Application application) throws Exception {
		LOG.info(" daemon start {} service at {}", application.getName(),
				new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date()));

		repository.startProcess(application.getStartScript());
	}

	@Override
	public boolean isProcessRunning(Application application) {
		// TODO Auto-generated method stub

		File pidDir = new File(application.getStartScript()).getParentFile();
		File pidFile = null;
		BufferedReader bufferedReader = null;
		try {
			if (pidDir != null && pidDir.exists() && pidDir.isDirectory()) {

				// pid file checker
				if (!application.getPidFile().contains("/")) {
					pidFile = new File(
							new File(application.getStartScript())
									.getParentFile(),
							application.getPidFile());
				} else {
					pidFile = new File(application.getPidFile());
				}

				LOG.debug("pid file : {} ", pidFile.getPath());

				if (pidFile.exists()) {
					// read pid file
					String line = "";
					bufferedReader = new BufferedReader(new FileReader(pidFile));

					while ((line = bufferedReader.readLine()) != null) {
						// check by grepping targeted process
						if (repository.isProcessRunning(line,
								application.getName(), pidFile.getName())) {
							LOG.debug("{} service running at {} with pid {} ",
									application.getName(),
									new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
											.format(new Date()), line);
							return true;
						}
					}
				}
			}
		} catch (Exception exception) {
			LOG.error("error occured with message {}", exception.getMessage());
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException ex) {
					LOG.error("error occured with message {}", ex.getMessage());
				}

			}
		}
		return false;
	}
}
