package com.firstwap.dispatcher.observer.service.runner;

import com.firstwap.dispatcher.observer.domain.Application;
import com.firstwap.dispatcher.observer.utility.ObserverException;

/**
 *
 * @author yakub
 *
 * @createdOn Sep 2, 2016 1:39:14 PM
 *
 */
public interface ComponentChecker {

	void checkProcesses() throws ObserverException;

	void processingApps(Application application) throws Exception;

	boolean isProcessRunning(Application application);

}
