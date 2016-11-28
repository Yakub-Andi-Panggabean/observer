package com.firstwap.dispatcher.observer.service;

import java.util.List;

import com.firstwap.dispatcher.observer.domain.ProcessDomain;
import com.firstwap.dispatcher.observer.utility.ObserverException;

/**
 *
 * @author yakub
 *
 * @createdOn Sep 6, 2016 10:11:51 AM
 *
 */
public interface ProcessService {

	List<ProcessDomain> fetchListProcess() throws Exception;

	void startProcess(String location) throws ObserverException;

	void killProcess(String pid, String stopScript) throws ObserverException;

	void killAllProcess(List<ProcessDomain> processes) throws ObserverException;

	void startAllProcess(List<ProcessDomain> processes)
			throws ObserverException;

}
