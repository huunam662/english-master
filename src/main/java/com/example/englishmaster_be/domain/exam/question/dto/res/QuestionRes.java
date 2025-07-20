package com.example.englishmaster_be.domain.exam.question.dto.res;

import com.example.englishmaster_be.common.constant.QuestionType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class QuestionRes {

    private UUID questionId;
    private UUID partId;
    private UUID topicId;
    private UUID answerCorrectId;
    private String questionContent;
    private String questionResult;
    private String contentAudio;
    private String contentImage;
    private Integer questionNumber;
    private Integer questionScore;
    private Integer numberChoice;
    private Integer numberOfQuestionsChild;
    private Boolean isQuestionParent;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private QuestionType questionType;
    private List<QuestionChildRes> questionsChildren;
}
