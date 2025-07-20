package com.example.englishmaster_be.domain.mock_test.mock_test.dto.res;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
public class MockTestKeyRes {

    private UUID mockTestId;

    public MockTestKeyRes(UUID mockTestId) {
        this.mockTestId = mockTestId;
    }
}
