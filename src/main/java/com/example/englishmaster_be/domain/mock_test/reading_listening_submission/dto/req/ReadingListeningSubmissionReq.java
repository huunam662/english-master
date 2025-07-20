package com.example.englishmaster_be.domain.mock_test.reading_listening_submission.dto.req;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
public class ReadingListeningSubmissionReq {

    private UUID resultMockTestId;

    private UUID partId;

    private UUID mockTestId;

    private Integer correctAnswer;

    private Integer score;

}
