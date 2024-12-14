package com.example.englishmaster_be.model.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnswerMatchingResponse {

    UUID id;

    String contentLeft;

    String contentRight;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    QuestionResponse question;
}
