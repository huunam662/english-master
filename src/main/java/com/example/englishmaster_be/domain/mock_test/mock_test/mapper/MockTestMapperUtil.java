package com.example.englishmaster_be.domain.mock_test.mock_test.mapper;

import com.example.englishmaster_be.common.constant.speaking_test.SpeakingErrorType;
import com.example.englishmaster_be.domain.exam.answer.mapper.AnswerMapper;
import com.example.englishmaster_be.domain.exam.answer.model.AnswerEntity;
import com.example.englishmaster_be.domain.exam.part.mapper.PartMapper;
import com.example.englishmaster_be.domain.exam.part.model.PartEntity;
import com.example.englishmaster_be.domain.exam.question.mapper.QuestionMapper;
import com.example.englishmaster_be.domain.mock_test.reading_listening_submission.dto.res.ReadingListeningDetailRes;
import com.example.englishmaster_be.domain.mock_test.reading_listening_submission.dto.res.ReadingListeningSubmissionRes;
import com.example.englishmaster_be.domain.mock_test.reading_listening_submission.model.ReadingListeningSubmissionEntity;
import com.example.englishmaster_be.domain.mock_test.speaking_submission.dto.res.speaking_error.SpeakingErrorRes;
import com.example.englishmaster_be.domain.mock_test.speaking_submission.dto.res.speaking_submission.SpeakingSubmissionRes;
import com.example.englishmaster_be.domain.mock_test.speaking_submission.dto.res.speaking_submission.SpeakingSubmissionResultRes;
import com.example.englishmaster_be.domain.mock_test.speaking_submission.mapper.SpeakingErrorMapper;
import com.example.englishmaster_be.domain.mock_test.speaking_submission.mapper.SpeakingSubmissionMapper;
import com.example.englishmaster_be.domain.mock_test.speaking_submission.model.SpeakingErrorEntity;
import com.example.englishmaster_be.domain.mock_test.speaking_submission.model.SpeakingSubmissionEntity;
import java.util.*;
import java.util.stream.Collectors;

public class MockTestMapperUtil {

    public static List<SpeakingSubmissionResultRes> mapToSpeakingSubmissionResults(Collection<SpeakingSubmissionEntity> speakingSubmissions){
        if(speakingSubmissions == null)
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


    public static List<ReadingListeningSubmissionRes> mapToReadingListeningSubmissionResults(Collection<ReadingListeningSubmissionEntity> entities){
        if(entities == null) return null;
        Map<PartEntity, List<ReadingListeningSubmissionEntity>> partSubmissionsMap = entities.stream().collect(
                Collectors.groupingBy(elm -> elm.getAnswerChoice().getQuestion().getPart())
        );
        List<ReadingListeningSubmissionRes> resResults = new ArrayList<>();
        List<PartEntity> parts = partSubmissionsMap.keySet().stream().sorted(Comparator.comparing(PartEntity::getPartName, Comparator.nullsLast(Comparator.naturalOrder()))).toList();
        for(PartEntity part : parts){
            List<ReadingListeningSubmissionEntity> submissions = partSubmissionsMap.getOrDefault(part, null);
            if(submissions == null) continue;
            ReadingListeningSubmissionRes rlsRes = new ReadingListeningSubmissionRes();
            rlsRes.setPart(PartMapper.INSTANCE.toPartBasicResponse(part));
            int totalScoreResultOfPart = 0;
            int totalCorrectOfPart = 0;
            rlsRes.setMockTestDetails(new ArrayList<>());
            for(ReadingListeningSubmissionEntity submission : submissions){
                ReadingListeningDetailRes rldRes = new ReadingListeningDetailRes();
                AnswerEntity answerChoice = submission.getAnswerChoice();
                rldRes.setQuestion(QuestionMapper.INSTANCE.toQuestionAnswersResponse(answerChoice.getQuestion()));
                rldRes.setAnswerChoice(AnswerMapper.INSTANCE.toAnswerResponse(answerChoice));
                rldRes.setIsCorrectAnswer(submission.getIsCorrectAnswer());
                rldRes.setScoreAchieved(submission.getScoreAchieved());
                rldRes.setAnswerContent(submission.getAnswerContent());
                if (submission.getIsCorrectAnswer()){
                    totalCorrectOfPart++;
                    totalScoreResultOfPart += submission.getScoreAchieved();
                }
                rlsRes.getMockTestDetails().add(rldRes);
            }
            rlsRes.setTotalCorrect(totalCorrectOfPart);
            rlsRes.setTotalScoreResult(totalScoreResultOfPart);
            resResults.add(rlsRes);
        }
        return resResults;
    }

}
