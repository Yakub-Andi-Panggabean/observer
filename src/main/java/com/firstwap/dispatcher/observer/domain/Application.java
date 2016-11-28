package com.firstwap.dispatcher.observer.domain;

/**
 *
 * @author yakub
 *
 * @createdOn Sep 2, 2016 4:17:36 PM
 *
 */
public class Application {

	protected String name;
	protected String startScript;
	protected String stopScript;
	protected String pidFile;
	protected Integer priority;
	protected boolean automatic;

	public Application() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public String getStopScript() {
		return stopScript;
	}

	public void setStopScript(String stopScript) {
		this.stopScript = stopScript;
	}

	public String getStartScript() {
		return startScript;
	}

	public void setStartScript(String startScript) {
		this.startScript = startScript;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isAutomatic() {
		return automatic;
	}

	public void setAutomatic(boolean automatic) {
		this.automatic = automatic;
	}

	public String getPidFile() {
		return pidFile;
	}

	public void setPidFile(String pidFile) {
		this.pidFile = pidFile;
	}

	@Override
	public String toString() {
		return "Application [name=" + name + ", startScript=" + startScript
				+ ", stopScript=" + stopScript + ", pidFile=" + pidFile
				+ ", priority=" + priority + ", automatic=" + automatic + "]";
	}

}
