package com.example.englishmaster_be.domain.mock_test.speaking_submission.dto.res.speaking_error;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BotFeedbackErrorRes {

    List<PronunciationFbRes> pronunciations;

    List<GrammarFluencyFbRes> grammars;

    List<GrammarFluencyFbRes> fluencies;

    List<VocabularyFbRes> vocabularies;

}
