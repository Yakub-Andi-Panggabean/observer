package com.firstwap.dispatcher.observer.service.runner.bean;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.firstwap.dispatcher.observer.service.runner.ComponentChecker;
import com.firstwap.dispatcher.observer.utility.OsChecker;

/**
 *
 * @author yakub
 *
 * @createdOn Sep 14, 2016 11:17:50 AM
 *
 */

@Component
public class ObserverDaemon implements Runnable {

	private static final Logger LOG = LoggerFactory
			.getLogger(ObserverDaemon.class);

	@Autowired()
	@Qualifier("linux")
	private ComponentChecker linuxComponentChecker;

	@Autowired
	@Qualifier("windows")
	private ComponentChecker windowsComponentChecker;

	@Value("${service_delay_time}")
	private int delay;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			try {
				if (OsChecker.isUnix()) {
					linuxComponentChecker.checkProcesses();
				} else if (OsChecker.isWindows()) {
					windowsComponentChecker.checkProcesses();
				}
				TimeUnit.SECONDS.sleep(delay);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				LOG.error("exception occured with message {}", e.getMessage());
			}

		}
	}
}
