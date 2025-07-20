package com.example.englishmaster_be.domain.mock_test.speaking_submission.dto.req;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;


@Data
@NoArgsConstructor
public class SpeakingQuestionAudioReq {

    private UUID questionId;

    private String audioUrl;

}
