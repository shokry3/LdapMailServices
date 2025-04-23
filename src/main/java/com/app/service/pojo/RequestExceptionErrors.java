package com.app.service.pojo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestExceptionErrors {
	@JsonProperty(value = "StatusCode")
	int statusCode;
	@JsonProperty(value = "StatusMessage")
	String statusMessage;
	Date time;

	public RequestExceptionErrors() {
	}

	public RequestExceptionErrors(int statusCode, String statusMessage, Date time) {
		super();
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
		this.time = time;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

}
