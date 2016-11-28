package com.firstwap.dispatcher.observer.utility;

import java.io.Serializable;
import java.util.List;

public class GenericMultipleResponses<T> extends GenericResponse implements
		Serializable {

	/**
	 *
	 */

	private static final long serialVersionUID = 4167468350578683041L;
	private List<T> contents;
	private String total;

	public GenericMultipleResponses() {
		super();
	}

	public GenericMultipleResponses(String code, List<T> contents,
			String description, String total) {
		super(code, description);
		this.contents = contents;
		this.total = total;
	}

	public List<T> getContents() {
		return contents;
	}

	public void setContents(List<T> contents) {
		this.contents = contents;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	@Override
	public String toString() {
		return "GenericMultipleResponses [contents=" + contents + ", total="
				+ total + "]";
	}

}
