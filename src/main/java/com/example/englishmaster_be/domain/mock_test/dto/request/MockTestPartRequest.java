package com.example.englishmaster_be.domain.mock_test.dto.request;

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
public class MockTestPartRequest {

    UUID partId;

    String partType;

    List<MockTestQuestionParentRequest> questionParentAnswers;

}
