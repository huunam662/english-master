package com.example.englishmaster_be.domain.question.dto.response;

import com.example.englishmaster_be.common.constant.QuestionType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionResponse {

    UUID questionId;

    UUID partId;

    UUID topicId;

    UUID answerCorrectId;

    String questionContent;

    String questionResult;

    String contentAudio;

    String contentImage;

    Integer questionNumber;

    Integer questionScore;

    Integer numberChoice;

    Integer numberOfQuestionsChild;

    Boolean isQuestionParent;

    QuestionType questionType;

    List<QuestionChildResponse> questionsChildren;
}
