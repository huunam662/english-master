package com.example.englishmaster_be.domain.mock_test.reading_listening_submission.util;

import com.example.englishmaster_be.domain.exam.answer.mapper.AnswerMapper;
import com.example.englishmaster_be.domain.exam.answer.model.AnswerEntity;
import com.example.englishmaster_be.domain.mock_test.reading_listening_submission.dto.res.ReadingListeningDetailRes;
import com.example.englishmaster_be.domain.mock_test.reading_listening_submission.dto.res.ReadingListeningSubmissionRes;
import com.example.englishmaster_be.domain.mock_test.reading_listening_submission.model.ReadingListeningSubmissionEntity;
import com.example.englishmaster_be.domain.exam.part.mapper.PartMapper;
import com.example.englishmaster_be.domain.exam.part.model.PartEntity;
import com.example.englishmaster_be.domain.exam.question.mapper.QuestionMapper;

import java.util.*;
import java.util.stream.Collectors;

public class ReadingListeningSubmissionUtil {

    public static List<ReadingListeningSubmissionRes> fillToReadingListeningSubmissionResults(Collection<ReadingListeningSubmissionEntity> entities){
        if(entities == null || entities.isEmpty()) return null;
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
