package com.firstwap.dispatcher.observer.utility;

public class GenericResponse {

	private String code;
	private String description;

	public GenericResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GenericResponse(String code, String description) {
		super();
		this.code = code;
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
