package com.example.englishmaster_be.domain.speaking_submission.util.speaking;

import com.example.englishmaster_be.common.constant.speaking_test.LevelSpeakerType;
import com.example.englishmaster_be.common.constant.speaking_test.SpeakingErrorType;
import com.example.englishmaster_be.domain.speaking_submission.dto.response.BotFeedbackErrorResponse;
import com.example.englishmaster_be.domain.speaking_submission.dto.response.GrammarFluencyFbResponse;
import com.example.englishmaster_be.domain.speaking_submission.dto.response.PronunciationFbResponse;
import com.example.englishmaster_be.domain.speaking_submission.dto.response.VocabularyFbResponse;
import com.example.englishmaster_be.domain.speaking_submission.model.SpeakingErrorEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
                                .pronunciationUrl(pronunciationError.getPronunciationUrl())
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
                                .pronunciationUrl(vocabularyError.getPronunciationUrl())
                                .build()
                );
            }
        }
        return speakingErrors;
    }
}
