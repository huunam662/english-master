package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Model.MockTest;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class MockTestResponse {
    private UUID mockTestID;
    private int score;
    private Time time;
    private UUID topicId;

    private String createAt;
    private String updateAt;

    private JSONObject userCreate;

    private JSONObject userUpdate;

    public MockTestResponse(MockTest mockTest) {
        this.mockTestID = mockTest.getMockTestId();
        this.score = mockTest.getScore();
        this.time = mockTest.getTime();
        this.topicId = mockTest.getTopic().getTopicId();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        this.createAt = sdf.format(Timestamp.valueOf(mockTest.getCreateAt()));
        this.updateAt = sdf.format(Timestamp.valueOf(mockTest.getUpdateAt()));

        userCreate = new JSONObject();
        userUpdate = new JSONObject();

        userCreate.put("User Id", mockTest.getUserCreate().getUserId());
        userCreate.put("User Name", mockTest.getUserCreate().getName());

        userUpdate.put("User Id", mockTest.getUserUpdate().getUserId());
        userUpdate.put("User Name", mockTest.getUserUpdate().getName());
    }
}
