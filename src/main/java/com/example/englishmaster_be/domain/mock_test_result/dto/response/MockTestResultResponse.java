package com.example.englishmaster_be.domain.mock_test_result.dto.response;

import com.example.englishmaster_be.domain.mock_test.dto.response.MockTestDetailResponse;
import com.example.englishmaster_be.domain.part.dto.response.PartBasicResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MockTestResultResponse {

    UUID mockTestResultId;

    PartBasicResponse part;

    Integer totalScoreResult;

    Integer totalCorrect;

    List<MockTestDetailResponse> mockTestDetails;
}
