package com.example.englishmaster_be.domain.mock_test.dto.response;

import com.example.englishmaster_be.domain.answer.dto.response.Answer1Response;
import com.example.englishmaster_be.domain.question.dto.response.QuestionAnswersResponse;
import com.example.englishmaster_be.domain.question.dto.response.Question2Response;
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

    Answer1Response answerChoice;

    Question2Response question;

}
