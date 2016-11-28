package com.firstwap.dispatcher.observer.service.bean;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.firstwap.dispatcher.observer.domain.Application;
import com.firstwap.dispatcher.observer.domain.Container;
import com.firstwap.dispatcher.observer.repository.ProcessRegistryRepository;
import com.firstwap.dispatcher.observer.service.ProcessRegistryService;
import com.firstwap.dispatcher.observer.utility.CmdUtil;
import com.firstwap.dispatcher.observer.utility.ObserverException;

/**
 *
 * @author yakub
 *
 * @createdOn Sep 15, 2016 9:57:32 AM
 *
 */

@Service
public class ProcessRegistryServiceBean implements ProcessRegistryService {

	private static final Logger LOG = LoggerFactory
			.getLogger(ProcessRegistryServiceBean.class.getName());

	@Autowired
	private ProcessRegistryRepository repository;

	@Value("${applications_list}")
	private String fileLocation;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.firstwap.dispatcher.observer.service.ProcessRegistryService#
	 * addNewRegistry(com.firstwap.dispatcher.observer.Application)
	 */
	@Override
	public void addNewRegistry(Application application)
			throws ObserverException {
		// TODO Auto-generated method stub
		File file = new File(fileLocation);
		try {

			if (file != null && file.exists() && file.isFile()) {
				Container container = repository.readRegistry(file);
				if (application != null) {

					// validate new application
					repository.validateApps(container, application,
							CmdUtil.INSERT_MODE);

					// adding new application to application list
					container.getApplications().add(application);
					LOG.debug("new container data : {} ", container);
					repository.writeRegistry(file, container);
				}
			}
		} catch (Exception exception) {
			throw new ObserverException(exception.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.firstwap.dispatcher.observer.service.ProcessRegistryService#
	 * removeRegistry(com.firstwap.dispatcher.observer.domain.Application)
	 */
	@Override
	public void removeRegistry(Application application)
			throws ObserverException {
		// TODO Auto-generated method stub
		try {
			File file = new File(fileLocation);
			Container container = repository.readRegistry(file);

			// validate new application
			repository
			.validateApps(container, application, CmdUtil.DELETE_MODE);

			for (int i = 0; i < container.getApplications().size(); i++) {
				if (application.getName().equals(
						container.getApplications().get(i).getName())) {
					container.getApplications().remove(i);

					LOG.debug("new container data : {} ", container);
					repository.writeRegistry(file, container);
				}
			}

		} catch (Exception ex) {
			throw new ObserverException(ex.getMessage());

		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.firstwap.dispatcher.observer.service.ProcessRegistryService#
	 * updateRegistry(com.firstwap.dispatcher.observer.domain.Application)
	 */
	@Override
	public void updateRegistry(Application application)
			throws ObserverException {
		// TODO Auto-generated method stub
		try {
			File file = new File(fileLocation);
			Container container = repository.readRegistry(file);

			// validate new application
			repository
			.validateApps(container, application, CmdUtil.UPDATE_MODE);

			for (int i = 0; i < container.getApplications().size(); i++) {
				if (application.getName().equals(
				// remove one
						container.getApplications().get(i).getName())) {
					container.getApplications().remove(i);

					// add new one
					container.getApplications().add(application);

					LOG.debug("new container data : {} ", container);
					repository.writeRegistry(file, container);
					break;
				}
			}

		} catch (Exception ex) {
			throw new ObserverException(ex.getMessage());

		}
	}
}
