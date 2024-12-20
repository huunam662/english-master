package com.example.englishmaster_be.domain.question.dto.response;

import com.example.englishmaster_be.domain.answer_matching.dto.response.AnswerMatchingBasicResponse;
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
public class QuestionMatchingDto {
    UUID questionId;
    String question;

    List<AnswerMatchingBasicResponse> options;
}
