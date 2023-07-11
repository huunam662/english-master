package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Model.MockTest;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class MockTestResponse {
    private UUID mockTestID;
    private int score;
    private Time time;
    private UUID topicId;

    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    private JSONObject userCreate;

    private JSONObject userUpdate;

    public MockTestResponse(MockTest mockTest) {
        this.mockTestID = mockTest.getMockTestId();
        this.score = mockTest.getScore();
        this.time = mockTest.getTime();
        this.topicId = mockTest.getTopic().getTopicId();

        this.createAt = mockTest.getCreateAt();
        this.updateAt = mockTest.getUpdateAt();

        userCreate = new JSONObject();
        userUpdate = new JSONObject();

        userCreate.put("User Id", mockTest.getUserCreate().getUserId());
        userCreate.put("User Name", mockTest.getUserCreate().getName());

        userUpdate.put("User Id", mockTest.getUserUpdate().getUserId());
        userUpdate.put("User Name", mockTest.getUserUpdate().getName());
    }
}
