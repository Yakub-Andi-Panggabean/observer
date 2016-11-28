package com.firstwap.dispatcher.observer.domain;

/**
 *
 * @author yakub
 *
 * @createdOn Sep 5, 2016 5:49:20 PM
 *
 */
public class ProcessDomain extends Application {

	private String user;
	private String status;
	private String pid;
	private String memoryUsage;
	private String cpuUsage;
	private String automaticStatus;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getMemoryUsage() {
		return memoryUsage;
	}

	public void setMemoryUsage(String memoryUsage) {
		this.memoryUsage = memoryUsage;
	}

	public String getCpuUsage() {
		return cpuUsage;
	}

	public void setCpuUsage(String cpuUsage) {
		this.cpuUsage = cpuUsage;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getAutomaticStatus() {
		return automaticStatus;
	}

	public void setAutomaticStatus(String automaticStatus) {
		this.automaticStatus = automaticStatus;
	}

	@Override
	public String toString() {
		return "ProcessDomain [user=" + user + ", status=" + status + ", pid="
				+ pid + ", memoryUsage=" + memoryUsage + ", cpuUsage="
				+ cpuUsage + ", automaticStatus=" + automaticStatus + ", name="
				+ name + ", startScript=" + startScript + ", stopScript="
				+ stopScript + ", pidFile=" + pidFile + ", priority="
				+ priority + ", automatic=" + automatic + "]";
	}

}
