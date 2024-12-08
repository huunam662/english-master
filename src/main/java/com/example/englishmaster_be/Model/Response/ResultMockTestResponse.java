package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Mapper.PartMapper;
import com.example.englishmaster_be.Model.ResultMockTest;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Objects;
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

    String createAt;

    String updateAt;

    int correctAnswer;

    int score;

    PartResponse partResponse;


    public ResultMockTestResponse(ResultMockTest resultMockTest) {

        if(Objects.isNull(resultMockTest)) return;

        this.resultMockTestId = resultMockTest.getResultMockTestId();
        this.mockTestId = resultMockTest.getMockTest().getMockTestId();
        this.partResponse = PartMapper.INSTANCE.partEntityToPartResponse(resultMockTest.getPart());
        this.correctAnswer = resultMockTest.getCorrectAnswer();
        this.score = resultMockTest.getScore();

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
        if(Objects.nonNull(resultMockTest.getCreateAt()))
            this.createAt = sdf.format(Timestamp.valueOf(resultMockTest.getCreateAt()));
        if(Objects.nonNull(resultMockTest.getUpdateAt()))
            this.updateAt = sdf.format(Timestamp.valueOf(resultMockTest.getUpdateAt()));

    }
}
