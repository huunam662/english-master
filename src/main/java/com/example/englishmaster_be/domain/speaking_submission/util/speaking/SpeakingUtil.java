package com.example.englishmaster_be.domain.speaking_submission.util.speaking;

import com.example.englishmaster_be.common.constant.speaking_test.LevelSpeakerType;
import com.example.englishmaster_be.common.constant.speaking_test.SpeakingErrorType;
import com.example.englishmaster_be.domain.part.mapper.PartMapper;
import com.example.englishmaster_be.domain.part.model.PartEntity;
import com.example.englishmaster_be.domain.speaking_submission.dto.response.speaking_error.*;
import com.example.englishmaster_be.domain.speaking_submission.dto.response.speaking_submission.SpeakingSubmissionResponse;
import com.example.englishmaster_be.domain.speaking_submission.dto.response.speaking_submission.SpeakingSubmissionResultResponse;
import com.example.englishmaster_be.domain.speaking_submission.mapper.SpeakingErrorMapper;
import com.example.englishmaster_be.domain.speaking_submission.mapper.SpeakingSubmissionMapper;
import com.example.englishmaster_be.domain.speaking_submission.model.SpeakingErrorEntity;
import com.example.englishmaster_be.domain.speaking_submission.model.SpeakingSubmissionEntity;

import java.util.*;
import java.util.stream.Collectors;

public class SpeakingUtil {

    public static LevelSpeakerType toLevelSpeaker(float reachedPercent){
        if(reachedPercent < 25) return LevelSpeakerType.Needs_Improvement;
        if(reachedPercent < 60) return LevelSpeakerType.Basic_Communicator;
        if(reachedPercent < 75) return LevelSpeakerType.Average_Communicator;
        if(reachedPercent < 90) return LevelSpeakerType.Good_Communicator;
        return LevelSpeakerType.Native_Communicator;
    }

    public static String parseToJsonPretty(String assistantContent){
        if(assistantContent == null || assistantContent.isEmpty()) return null;
        int firstIndexCurlyBracket = assistantContent.indexOf("{");
        int lastIndexCurlyBracket = assistantContent.lastIndexOf("}");
        return assistantContent.substring(firstIndexCurlyBracket, lastIndexCurlyBracket + 1);
    }

    public static List<SpeakingErrorEntity> toSpeakingErrors(UUID speakingSubmissionId, BotFeedbackErrorResponse feedbackError){
        if(speakingSubmissionId == null || feedbackError == null)
            return new ArrayList<>();
        List<SpeakingErrorEntity> speakingErrors = new ArrayList<>();
        List<PronunciationFbResponse> pronunciationErrors = feedbackError.getPronunciations();
        if(pronunciationErrors != null){
            for(PronunciationFbResponse pronunciationError : pronunciationErrors){
                speakingErrors.add(
                        SpeakingErrorEntity.builder()
                                .id(UUID.randomUUID())
                                .speakingSubmissionId(speakingSubmissionId)
                                .speakingErrorType(SpeakingErrorType.Pronunciation)
                                .word(pronunciationError.getWord())
                                .wordRecommend(pronunciationError.getWordRecommend())
                                .pronunciation(pronunciationError.getPronunciation())
                                .feedback(pronunciationError.getFeedback())
                                .build()
                );
            }
        }
        List<GrammarFluencyFbResponse> grammarErrors = feedbackError.getGrammars();
        if(grammarErrors != null){
            for(GrammarFluencyFbResponse grammarError : grammarErrors){
                speakingErrors.add(
                        SpeakingErrorEntity.builder()
                                .id(UUID.randomUUID())
                                .speakingSubmissionId(speakingSubmissionId)
                                .speakingErrorType(SpeakingErrorType.Grammar)
                                .word(grammarError.getWord())
                                .wordRecommend(grammarError.getWordRecommend())
                                .feedback(grammarError.getFeedback())
                                .build()
                );
            }
        }
        List<GrammarFluencyFbResponse> fluencyErrors = feedbackError.getFluencies();
        if(fluencyErrors != null){
            for(GrammarFluencyFbResponse fluencyError : fluencyErrors){
                speakingErrors.add(
                        SpeakingErrorEntity.builder()
                                .id(UUID.randomUUID())
                                .speakingSubmissionId(speakingSubmissionId)
                                .speakingErrorType(SpeakingErrorType.Fluency)
                                .word(fluencyError.getWord())
                                .wordRecommend(fluencyError.getWordRecommend())
                                .feedback(fluencyError.getFeedback())
                                .build()
                );
            }
        }
        List<VocabularyFbResponse> vocabularyErrors = feedbackError.getVocabularies();
        if(vocabularyErrors != null){
            for(VocabularyFbResponse vocabularyError : vocabularyErrors){
                speakingErrors.add(
                        SpeakingErrorEntity.builder()
                                .id(UUID.randomUUID())
                                .speakingSubmissionId(speakingSubmissionId)
                                .speakingErrorType(SpeakingErrorType.Vocabulary)
                                .wordRecommend(vocabularyError.getWordRecommend())
                                .pronunciation(vocabularyError.getPronunciation())
                                .build()
                );
            }
        }
        return speakingErrors;
    }

    public static List<SpeakingSubmissionResultResponse> fillToSpeakingSubmissionResults(List<SpeakingSubmissionEntity> speakingSubmissions){
        if(speakingSubmissions == null || speakingSubmissions.isEmpty())
            return null;

        List<SpeakingSubmissionResultResponse> speakingSubmissionResults = new ArrayList<>();
        Map<PartEntity, List<SpeakingSubmissionEntity>> partSpeakingSubmissionsGroup = speakingSubmissions.stream().collect(
                Collectors.groupingBy(elm -> elm.getQuestion().getPart())
        );
        Set<PartEntity> partKeys = partSpeakingSubmissionsGroup.keySet().stream().sorted(Comparator.comparing(PartEntity::getPartName)).collect(Collectors.toCollection(LinkedHashSet::new));
        for(PartEntity part : partKeys){
            SpeakingSubmissionResultResponse speakingSubmissionResult = new SpeakingSubmissionResultResponse();
            speakingSubmissionResult.setPart(PartMapper.INSTANCE.toPartTopicResponse(part));
            List<SpeakingSubmissionEntity> speakingSubmissionsOfPart = partSpeakingSubmissionsGroup.getOrDefault(part, null);
            if(speakingSubmissionsOfPart == null) continue;
            speakingSubmissionResult.setSpeakingSubmissions(new ArrayList<>());
            for(SpeakingSubmissionEntity speakingSubmissionElm : speakingSubmissionsOfPart){
                SpeakingSubmissionResponse speakingSubmissionResponse = SpeakingSubmissionMapper.INSTANCE.toSpeakingSubmissionResponse(speakingSubmissionElm);
                speakingSubmissionResult.getSpeakingSubmissions().add(speakingSubmissionResponse);
                SpeakingErrorResponse speakingErrorResponse = new SpeakingErrorResponse();
                speakingErrorResponse.setSpeakingErrorTypes(Arrays.stream(SpeakingErrorType.values()).toList());
                speakingSubmissionResponse.setSpeakingErrors(speakingErrorResponse);
                Set<SpeakingErrorEntity> speakingErrors = speakingSubmissionElm.getSpeakingErrors();
                if(speakingErrors == null) continue;
                Map<SpeakingErrorType, List<SpeakingErrorEntity>> speakingErrorTypeErrorsGroup = speakingErrors.stream().collect(
                        Collectors.groupingBy(SpeakingErrorEntity::getSpeakingErrorType)
                );
                List<SpeakingErrorEntity> pronunciationErrors = speakingErrorTypeErrorsGroup.getOrDefault(SpeakingErrorType.Pronunciation, new ArrayList<>());
                List<SpeakingErrorEntity> grammarErrors = speakingErrorTypeErrorsGroup.getOrDefault(SpeakingErrorType.Grammar, new ArrayList<>());
                List<SpeakingErrorEntity> fluencyErrors = speakingErrorTypeErrorsGroup.getOrDefault(SpeakingErrorType.Fluency, new ArrayList<>());
                List<SpeakingErrorEntity> vocabularyErrors = speakingErrorTypeErrorsGroup.getOrDefault(SpeakingErrorType.Vocabulary, new ArrayList<>());
                speakingErrorResponse.setPronunciations(SpeakingErrorMapper.INSTANCE.toPronunciationErrorResponseList(pronunciationErrors));
                speakingErrorResponse.setGrammars(SpeakingErrorMapper.INSTANCE.toGrammarFluencyErrorResponseList(grammarErrors));
                speakingErrorResponse.setFluencies(SpeakingErrorMapper.INSTANCE.toGrammarFluencyErrorResponseList(fluencyErrors));
                speakingErrorResponse.setVocabularies(SpeakingErrorMapper.INSTANCE.toVocabularyErrorResponseList(vocabularyErrors));
            }
            speakingSubmissionResults.add(speakingSubmissionResult);
        }

        return speakingSubmissionResults;

    }

}
