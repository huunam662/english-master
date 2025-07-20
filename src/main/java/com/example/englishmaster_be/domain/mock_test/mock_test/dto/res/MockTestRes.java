package com.example.englishmaster_be.domain.mock_test.mock_test.dto.res;

import com.example.englishmaster_be.domain.exam.topic.topic.dto.res.TopicAndTypeRes;
import lombok.*;

import java.time.LocalTime;
import java.util.*;


@Data
@NoArgsConstructor
public class MockTestRes {

    private UUID mockTestId;

    private Integer totalScoreParts;

    private Integer totalScoreCorrect;

    private Integer totalQuestionsParts;

    private Integer totalQuestionsFinish;

    private Integer totalQuestionsSkip;

    private Integer totalAnswersCorrect;

    private Integer totalAnswersWrong;

    private Float answersCorrectPercent;

    private LocalTime workTime;

    private LocalTime finishTime;

    private TopicAndTypeRes topic;

}
