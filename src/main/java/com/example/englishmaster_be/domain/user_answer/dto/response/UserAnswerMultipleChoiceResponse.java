package com.example.englishmaster_be.domain.user_answer.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAnswerMultipleChoiceResponse {
    boolean correct;
    List<String> values;

    List<String> expected;

}
