package com.example.englishmaster_be.domain.mock_test.speaking_submission.mapper;

import com.example.englishmaster_be.domain.mock_test.speaking_submission.dto.res.speaking_error.GrammarFluencyErrorRes;
import com.example.englishmaster_be.domain.mock_test.speaking_submission.dto.res.speaking_error.PronunciationErrorRes;
import com.example.englishmaster_be.domain.mock_test.speaking_submission.dto.res.speaking_error.VocabularyErrorRes;
import com.example.englishmaster_be.domain.mock_test.speaking_submission.model.SpeakingErrorEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import java.util.Collection;
import java.util.List;

@Mapper(builder = @Builder(disableBuilder = true))
public interface SpeakingErrorMapper {

    SpeakingErrorMapper INSTANCE = Mappers.getMapper(SpeakingErrorMapper.class);

    @Mapping(target = "speakingErrorId", source = "id")
    PronunciationErrorRes toPronunciationErrorResponse(SpeakingErrorEntity speakingError);

    List<PronunciationErrorRes> toPronunciationErrorResponseList(Collection<SpeakingErrorEntity> speakingErrorList);

    @Mapping(target = "speakingErrorId", source = "id")
    GrammarFluencyErrorRes toGrammarFluencyErrorResponse(SpeakingErrorEntity speakingError);

    List<GrammarFluencyErrorRes> toGrammarFluencyErrorResponseList(Collection<SpeakingErrorEntity> speakingErrorList);

    @Mapping(target = "speakingErrorId", source = "id")
    VocabularyErrorRes toVocabularyErrorResponse(SpeakingErrorEntity speakingError);

    List<VocabularyErrorRes> toVocabularyErrorResponseList(Collection<SpeakingErrorEntity> speakingErrorList);
}
