package com.example.englishmaster_be.domain.speaking_submission.dto.response.speaking_error;

import com.example.englishmaster_be.common.constant.speaking_test.SpeakingErrorType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PronunciationErrorResponse extends PronunciationFbResponse{

    UUID speakingErrorId;

    UUID speakingSubmissionId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    SpeakingErrorType speakingErrorType;

}
