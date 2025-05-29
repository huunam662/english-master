package com.example.englishmaster_be.domain.question.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateQuestionParentResponse {

    boolean error;

    String errorMessage;

    int parentIndex;

    List<CreateQuestionChildResponse> createQChildErrors;
}
