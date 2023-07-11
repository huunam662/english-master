package com.example.englishmaster_be.Model;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

public class ResponseModel {
    private String message;
    private Object responseData;
    private String status;
    private String timeStamp;
    private String violations;

    public ResponseModel() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        timeStamp = sdf.format(Timestamp.valueOf(LocalDateTime.now()));
    }

    public ResponseModel(String message, Object responseData, String status, String violations) {
        this.message = message;
        this.responseData = responseData;
        this.status = status;
        this.violations = violations;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        timeStamp = sdf.format(Timestamp.valueOf(LocalDateTime.now()));
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

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getViolations() {
		return violations;
	}

	public void setViolations(String violations) {
		this.violations = violations;
	}
    

}
