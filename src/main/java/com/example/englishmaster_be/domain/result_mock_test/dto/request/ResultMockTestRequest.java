package com.example.englishmaster_be.domain.result_mock_test.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResultMockTestRequest {

    UUID resultMockTestId;

    UUID partId;

    UUID mockTestId;

    Integer correctAnswer;

    Integer score;

}
