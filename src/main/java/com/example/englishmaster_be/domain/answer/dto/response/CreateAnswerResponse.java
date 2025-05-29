package com.example.englishmaster_be.domain.answer.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateAnswerResponse {

    boolean isError;

    String errorMessage;

    int answerIndex;

}
