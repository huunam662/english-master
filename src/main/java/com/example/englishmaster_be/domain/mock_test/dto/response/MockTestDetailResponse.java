package com.example.englishmaster_be.domain.mock_test.dto.response;

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

    UUID detailMockTestId;

    UUID answerId;

    String answerContent;

    Boolean correctAnswer;

    Integer scoreAnswer;

}
