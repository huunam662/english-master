package com.example.englishmaster_be.domain.speaking_submission.dto.response.speaking_error;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BotFeedbackErrorResponse {

    List<PronunciationFbResponse> pronunciations;

    List<GrammarFluencyFbResponse> grammars;

    List<GrammarFluencyFbResponse> fluencies;

    List<VocabularyFbResponse> vocabularies;

}
