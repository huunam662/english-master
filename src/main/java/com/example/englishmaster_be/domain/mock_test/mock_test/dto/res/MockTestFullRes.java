package com.example.englishmaster_be.domain.mock_test.mock_test.dto.res;

import com.example.englishmaster_be.domain.exam.topic.topic.dto.res.TopicAndTypeRes;
import com.example.englishmaster_be.domain.user.user.dto.res.UserRes;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class MockTestFullRes {

    private UUID mockTestId;
    private Integer totalScore;
    private Integer totalQuestionsFinish;
    private Integer totalQuestionsSkip;
    private Integer totalAnswersCorrect;
    private Integer totalAnswersWrong;
    private Float answersCorrectPercent;
    private LocalTime workTime;
    private LocalTime finishTime;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private TopicAndTypeRes topic;
    private UserRes user;

}
