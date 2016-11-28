package com.firstwap.dispatcher.observer.service;

import com.firstwap.dispatcher.observer.domain.Application;
import com.firstwap.dispatcher.observer.utility.ObserverException;

/**
 *
 * @author yakub
 *
 * @createdOn Sep 15, 2016 9:53:23 AM
 *
 */
public interface ProcessRegistryService {

	void addNewRegistry(Application application) throws ObserverException;

	void removeRegistry(Application application) throws ObserverException;

	void updateRegistry(Application application) throws ObserverException;
}
