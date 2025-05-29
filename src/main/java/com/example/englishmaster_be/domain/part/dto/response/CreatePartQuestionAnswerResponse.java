package com.example.englishmaster_be.domain.part.dto.response;

import com.example.englishmaster_be.domain.question.dto.response.CreateQuestionParentResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatePartQuestionAnswerResponse {

    UUID partId;

    boolean error;

    String errorMessage;

    List<CreateQuestionParentResponse> createQParentErrors;

}
