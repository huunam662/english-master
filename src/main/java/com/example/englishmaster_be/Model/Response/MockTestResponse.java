package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Model.MockTest;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.json.simple.JSONObject;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MockTestResponse {

    UUID mockTestID;

    UUID topicId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss")
    LocalDateTime createAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss")
    LocalDateTime updateAt;

    UserBasicResponse userCreate;

    UserBasicResponse userUpdate;

    int correctAnswers;

    int score;

    Time time;

    Object user;

    public MockTestResponse(MockTest mockTest) {

        if(Objects.isNull(mockTest)) return;

        this.mockTestID = mockTest.getMockTestId();
        this.score = mockTest.getScore();
        this.time = mockTest.getTime();
        this.topicId = mockTest.getTopic().getTopicId();
        this.correctAnswers = mockTest.getCorrectAnswers();

        Map<String, Object> userObj = new HashMap<>();
        userObj.put("user_id", mockTest.getUser().getUserId());
        userObj.put("user_name", mockTest.getUser().getName());
        this.user = userObj;

        this.createAt = mockTest.getCreateAt();
        this.updateAt = mockTest.getUpdateAt();

        userCreate = UserBasicResponse.builder()
                .userId(mockTest.getUserCreate().getUserId())
                .name(mockTest.getUserCreate().getName())
                .build();

        userUpdate = UserBasicResponse.builder()
                .userId(mockTest.getUserUpdate().getUserId())
                .name(mockTest.getUserUpdate().getName())
                .build();

    }

}
