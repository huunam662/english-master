package com.example.englishmaster_be.domain.speaking_submission.mapper;

import com.example.englishmaster_be.domain.speaking_submission.dto.response.speaking_error.GrammarFluencyErrorResponse;
import com.example.englishmaster_be.domain.speaking_submission.dto.response.speaking_error.PronunciationErrorResponse;
import com.example.englishmaster_be.domain.speaking_submission.dto.response.speaking_error.VocabularyErrorResponse;
import com.example.englishmaster_be.domain.speaking_submission.model.SpeakingErrorEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface SpeakingErrorMapper {

    SpeakingErrorMapper INSTANCE = Mappers.getMapper(SpeakingErrorMapper.class);

    @Mapping(target = "speakingErrorId", source = "id")
    PronunciationErrorResponse toPronunciationErrorResponse(SpeakingErrorEntity speakingError);

    List<PronunciationErrorResponse> toPronunciationErrorResponseList(List<SpeakingErrorEntity> speakingErrorList);

    @Mapping(target = "speakingErrorId", source = "id")
    GrammarFluencyErrorResponse toGrammarFluencyErrorResponse(SpeakingErrorEntity speakingError);

    List<GrammarFluencyErrorResponse> toGrammarFluencyErrorResponseList(List<SpeakingErrorEntity> speakingErrorList);

    @Mapping(target = "speakingErrorId", source = "id")
    VocabularyErrorResponse toVocabularyErrorResponse(SpeakingErrorEntity speakingError);

    List<VocabularyErrorResponse> toVocabularyErrorResponseList(List<SpeakingErrorEntity> speakingErrorList);
}
