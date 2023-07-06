package com.example.englishmaster_be.Model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

@Getter
@Setter
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

}
