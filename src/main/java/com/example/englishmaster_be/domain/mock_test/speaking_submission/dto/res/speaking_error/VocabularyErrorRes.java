package com.example.englishmaster_be.domain.mock_test.speaking_submission.dto.res.speaking_error;

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
public class VocabularyErrorRes extends VocabularyFbRes {

    UUID speakingErrorId;

    UUID speakingSubmissionId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    SpeakingErrorType speakingErrorType;

}
