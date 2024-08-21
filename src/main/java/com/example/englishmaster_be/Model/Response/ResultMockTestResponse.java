package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Model.ResultMockTest;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.json.simple.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResultMockTestResponse {
    UUID resultMockTestId;
    UUID mockTestId;
    UUID partId;
    int correctAnswer;
    int score;
    String createAt;
    JSONObject userCreate;
    String updateAt;
    JSONObject userUpdate;

    public ResultMockTestResponse(ResultMockTest resultMockTest) {
        this.resultMockTestId = resultMockTest.getResultMockTestId();
        this.mockTestId = resultMockTest.getMockTest().getMockTestId();
        this.partId = resultMockTest.getPart().getPartId();
        this.correctAnswer = resultMockTest.getCorrectAnswer();
        this.score = resultMockTest.getScore();

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");

        this.createAt = sdf.format(Timestamp.valueOf(resultMockTest.getCreateAt()));
        this.updateAt = sdf.format(Timestamp.valueOf(resultMockTest.getUpdateAt()));

        userCreate = new JSONObject();
        userUpdate = new JSONObject();

        userCreate.put("User Id", resultMockTest.getUserCreate().getUserId());
        userCreate.put("User Name", resultMockTest.getUserCreate().getName());

        userUpdate.put("User Id", resultMockTest.getUserUpdate().getUserId());
        userUpdate.put("User Name", resultMockTest.getUserUpdate().getName());
    }
}
