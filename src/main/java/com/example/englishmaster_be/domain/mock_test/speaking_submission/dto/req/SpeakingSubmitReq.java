package com.example.englishmaster_be.domain.mock_test.speaking_submission.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class SpeakingSubmitReq {

    @NotNull(message = "Mock test id is required.")
    private UUID mockTestId;

    @NotNull(message = "Speaking submit contents is required.")
    private List<SpeakingQuestionAudioReq> contents;

}
