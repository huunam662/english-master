package com.example.englishmaster_be.domain.mock_test.dto.response;

import com.example.englishmaster_be.domain.mock_test_result.dto.response.MockTestResultResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MockTestInforResponse {
    MockTest1Response mockTestResponse;
    List<MockTestResultResponse> mockTestResultResponses;
}
