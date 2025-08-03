package com.example.englishmaster_be.domain.speaking_submission.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpeakingSubmitRequest {

    @NotNull(message = "Mock test id is required.")
    UUID mockTestId;

    @NotNull(message = "Speaking submit contents is required.")
    List<SpeakingQuestionAudioRequest> contents;

}
