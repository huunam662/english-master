package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Model.MockTest;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.json.simple.JSONObject;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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

    String createAt;

    String updateAt;

    JSONObject userCreate;

    JSONObject userUpdate;

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

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
        if(Objects.nonNull(mockTest.getCreateAt()))
            this.createAt = sdf.format(Timestamp.valueOf(mockTest.getCreateAt()));
        if(Objects.nonNull(mockTest.getUpdateAt()))
            this.updateAt = sdf.format(Timestamp.valueOf(mockTest.getUpdateAt()));

        userCreate = new JSONObject();
        userUpdate = new JSONObject();

        userCreate.put("User Id", mockTest.getUserCreate().getUserId());
        userCreate.put("User Name", mockTest.getUserCreate().getName());

        userUpdate.put("User Id", mockTest.getUserUpdate().getUserId());
        userUpdate.put("User Name", mockTest.getUserUpdate().getName());
    }

}
