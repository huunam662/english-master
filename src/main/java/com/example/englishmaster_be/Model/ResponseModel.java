package com.example.englishmaster_be.Model;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;

public class ResponseModel {
    private String message;
    private Object responseData;
    private String status;
    private Instant timeStamp;
    private String violations;

    public ResponseModel() {
        this.timeStamp = Instant.now();
    }

    public ResponseModel(String message, Object responseData, String status, String violations) {
        this.message = message;
        this.responseData = responseData;
        this.status = status;
        this.violations = violations;
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
      	this.timeStamp = Instant.now();
    }

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getResponseData() {
		return responseData;
	}

	public void setResponseData(Object responseData) {
		this.responseData = responseData;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Instant getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Instant timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getViolations() {
		return violations;
	}

	public void setViolations(String violations) {
		this.violations = violations;
	}
    

}
