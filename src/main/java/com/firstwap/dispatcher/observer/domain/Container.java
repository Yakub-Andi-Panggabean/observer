package com.firstwap.dispatcher.observer.domain;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author yakub
 *
 * @createdOn Sep 2, 2016 3:19:42 PM
 *
 */
public class Container implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 904065585944697506L;
	private List<Application> applications;
	private String version;

	public List<Application> getApplications() {
		return applications;
	}

	public void setApplications(List<Application> applications) {
		this.applications = applications;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "Container [applications=" + applications + ", version="
				+ version + "]";
	}

}
