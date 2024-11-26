package com.example.englishmaster_be.model.response;

import com.example.englishmaster_be.model.ResultMockTest;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
    PartResponse partResponse;
    int correctAnswer;
    int score;
    String createAt;
    String updateAt;


    public ResultMockTestResponse(ResultMockTest resultMockTest) {
        this.resultMockTestId = resultMockTest.getResultMockTestId();
        this.mockTestId = resultMockTest.getMockTest().getMockTestId();
        this.partResponse = new PartResponse(resultMockTest.getPart());
        this.correctAnswer = resultMockTest.getCorrectAnswer();
        this.score = resultMockTest.getScore();

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");

        this.createAt = sdf.format(Timestamp.valueOf(resultMockTest.getCreateAt()));
        this.updateAt = sdf.format(Timestamp.valueOf(resultMockTest.getUpdateAt()));

    }
}
