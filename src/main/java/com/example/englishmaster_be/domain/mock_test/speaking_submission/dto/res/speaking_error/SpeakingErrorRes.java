package com.example.englishmaster_be.domain.mock_test.speaking_submission.dto.res.speaking_error;

import com.example.englishmaster_be.common.constant.speaking_test.SpeakingErrorType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpeakingErrorRes {

    List<SpeakingErrorType> speakingErrorTypes;

    List<PronunciationErrorRes> pronunciations;

    List<GrammarFluencyErrorRes> grammars;

    List<GrammarFluencyErrorRes> fluencies;

    List<VocabularyErrorRes> vocabularies;

}
