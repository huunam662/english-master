package com.example.englishmaster_be.domain.speaking_submission.dto.response.speaking_error;

import com.example.englishmaster_be.common.constant.speaking_test.SpeakingErrorType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpeakingErrorResponse {

    List<SpeakingErrorType> speakingErrorTypes;

    List<PronunciationErrorResponse> pronunciations;

    List<GrammarFluencyErrorResponse> grammars;

    List<GrammarFluencyErrorResponse> fluencies;

    List<VocabularyErrorResponse> vocabularies;

}
