package com.example.englishmaster_be.domain.mock_test.speaking_submission.util.speaking;

import com.example.englishmaster_be.common.constant.speaking_test.LevelSpeakerType;
import com.example.englishmaster_be.common.constant.speaking_test.SpeakingErrorType;
import com.example.englishmaster_be.domain.mock_test.speaking_submission.dto.res.speaking_error.*;
import com.example.englishmaster_be.domain.mock_test.speaking_submission.mapper.SpeakingSubmissionMapper;
import com.example.englishmaster_be.domain.exam.part.mapper.PartMapper;
import com.example.englishmaster_be.domain.exam.part.model.PartEntity;
import com.example.englishmaster_be.domain.mock_test.speaking_submission.dto.res.speaking_submission.SpeakingSubmissionRes;
import com.example.englishmaster_be.domain.mock_test.speaking_submission.dto.res.speaking_submission.SpeakingSubmissionResultRes;
import com.example.englishmaster_be.domain.mock_test.speaking_submission.mapper.SpeakingErrorMapper;
import com.example.englishmaster_be.domain.mock_test.speaking_submission.model.SpeakingErrorEntity;
import com.example.englishmaster_be.domain.mock_test.speaking_submission.model.SpeakingSubmissionEntity;
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

    public static List<SpeakingErrorEntity> toSpeakingErrors(UUID speakingSubmissionId, BotFeedbackErrorRes feedbackError){
        if(speakingSubmissionId == null || feedbackError == null)
            return new ArrayList<>();
        List<SpeakingErrorEntity> speakingErrors = new ArrayList<>();
        List<PronunciationFbRes> pronunciationErrors = feedbackError.getPronunciations();
        if(pronunciationErrors != null){
            for(PronunciationFbRes pronunciationError : pronunciationErrors){
                SpeakingErrorEntity speakingError = new SpeakingErrorEntity();
                speakingError.setId(UUID.randomUUID());
                speakingError.setSpeakingSubmissionId(speakingSubmissionId);
                speakingError.setSpeakingErrorType(SpeakingErrorType.Pronunciation);
                speakingError.setWord(pronunciationError.getWord());
                speakingError.setWordRecommend(pronunciationError.getWordRecommend());
                speakingError.setFeedback(pronunciationError.getFeedback());
                speakingErrors.add(speakingError);
            }
        }
        List<GrammarFluencyFbRes> grammarErrors = feedbackError.getGrammars();
        if(grammarErrors != null){
            for(GrammarFluencyFbRes grammarError : grammarErrors){
                SpeakingErrorEntity speakingError = new SpeakingErrorEntity();
                speakingError.setId(UUID.randomUUID());
                speakingError.setSpeakingSubmissionId(speakingSubmissionId);
                speakingError.setSpeakingErrorType(SpeakingErrorType.Grammar);
                speakingError.setWord(grammarError.getWord());
                speakingError.setWordRecommend(grammarError.getWordRecommend());
                speakingError.setFeedback(grammarError.getFeedback());
                speakingErrors.add(speakingError);
            }
        }
        List<GrammarFluencyFbRes> fluencyErrors = feedbackError.getFluencies();
        if(fluencyErrors != null){
            for(GrammarFluencyFbRes fluencyError : fluencyErrors){
                SpeakingErrorEntity speakingError = new SpeakingErrorEntity();
                speakingError.setId(UUID.randomUUID());
                speakingError.setSpeakingSubmissionId(speakingSubmissionId);
                speakingError.setSpeakingErrorType(SpeakingErrorType.Fluency);
                speakingError.setWord(fluencyError.getWord());
                speakingError.setWordRecommend(fluencyError.getWordRecommend());
                speakingError.setFeedback(fluencyError.getFeedback());
                speakingErrors.add(speakingError);
            }
        }
        List<VocabularyFbRes> vocabularyErrors = feedbackError.getVocabularies();
        if(vocabularyErrors != null){
            for(VocabularyFbRes vocabularyError : vocabularyErrors){
                SpeakingErrorEntity speakingError = new SpeakingErrorEntity();
                speakingError.setId(UUID.randomUUID());
                speakingError.setSpeakingSubmissionId(speakingSubmissionId);
                speakingError.setSpeakingErrorType(SpeakingErrorType.Vocabulary);
                speakingError.setWordRecommend(vocabularyError.getWordRecommend());
                speakingError.setPronunciation(vocabularyError.getPronunciation());
                speakingErrors.add(speakingError);
            }
        }
        return speakingErrors;
    }

    public static List<SpeakingSubmissionResultRes> fillToSpeakingSubmissionResults(Collection<SpeakingSubmissionEntity> speakingSubmissions){
        if(speakingSubmissions == null || speakingSubmissions.isEmpty())
            return null;

        List<SpeakingSubmissionResultRes> speakingSubmissionResults = new ArrayList<>();
        Map<PartEntity, List<SpeakingSubmissionEntity>> partSpeakingSubmissionsGroup = speakingSubmissions.stream().collect(
                Collectors.groupingBy(elm -> elm.getQuestion().getPart())
        );
        Set<PartEntity> partKeys = partSpeakingSubmissionsGroup.keySet().stream().sorted(Comparator.comparing(PartEntity::getPartName)).collect(Collectors.toCollection(LinkedHashSet::new));
        for(PartEntity part : partKeys){
            SpeakingSubmissionResultRes speakingSubmissionResult = new SpeakingSubmissionResultRes();
            speakingSubmissionResult.setPart(PartMapper.INSTANCE.toPartTopicResponse(part));
            List<SpeakingSubmissionEntity> speakingSubmissionsOfPart = partSpeakingSubmissionsGroup.getOrDefault(part, null);
            if(speakingSubmissionsOfPart == null) continue;
            speakingSubmissionsOfPart = speakingSubmissionsOfPart.stream().sorted(Comparator.comparing(s -> s.getQuestion().getQuestionNumber(), Comparator.nullsLast(Comparator.naturalOrder()))).toList();
            speakingSubmissionResult.setSpeakingSubmissions(new ArrayList<>());
            for(SpeakingSubmissionEntity speakingSubmissionElm : speakingSubmissionsOfPart){
                SpeakingSubmissionRes speakingSubmissionResponse = SpeakingSubmissionMapper.INSTANCE.toSpeakingSubmissionResponse(speakingSubmissionElm);
                speakingSubmissionResult.getSpeakingSubmissions().add(speakingSubmissionResponse);
                SpeakingErrorRes speakingErrorResponse = new SpeakingErrorRes();
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
