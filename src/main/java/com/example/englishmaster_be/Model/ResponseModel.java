package com.example.englishmaster_be.Model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class ResponseModel {
    private String message;
    private Object responseData;
    private String status;
    private Timestamp timeStamp;
    private String violations;

    public ResponseModel() {
        timeStamp = Timestamp.valueOf(LocalDateTime.now());
    }

    public ResponseModel(String message, Object responseData, String status, String violations) {
        this.message = message;
        this.responseData = responseData;
        this.status = status;
        this.violations = violations;
        timeStamp = Timestamp.valueOf(LocalDateTime.now());
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

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getViolations() {
        return violations;
    }

    public void setViolations(String violations) {
        this.violations = violations;
    }
}
