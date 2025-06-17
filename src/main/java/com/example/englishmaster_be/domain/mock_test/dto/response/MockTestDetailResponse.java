package com.example.englishmaster_be.domain.mock_test.dto.response;

import com.example.englishmaster_be.domain.answer.dto.response.AnswerBasicResponse;
import com.example.englishmaster_be.domain.question.dto.response.QuestionAnswersResponse;
import com.example.englishmaster_be.domain.question.dto.response.QuestionReadingListeningResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MockTestDetailResponse {

    UUID mockTestDetailId;

    String answerContent;

    String answerCorrectContent;

    Boolean isCorrectAnswer;

    Integer scoreAchieved;

    QuestionAnswersResponse questionChild;

    AnswerBasicResponse answerChoice;

    QuestionReadingListeningResponse question;

}
