package com.example.englishmaster_be.domain.speaking_submission.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpeakingQuestionAudioRequest {

    UUID questionId;

    String audioUrl;

}
