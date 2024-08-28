package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Exception.CustomException;
import com.example.englishmaster_be.Exception.Error;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.IAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AnswerServiceImpl implements IAnswerService {
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private DetailMockTestRepository detailMockTestRepository;

    @Override
    public void createAnswer(Answer answer) {
        answerRepository.save(answer);
    }

//    @Override
//    public AnswerResponse createAnswer(Answer answer) {
//
//    }

    @Override
    public boolean existQuestion(Answer answer, Question question) {
        return answer.getQuestion().equals(question);
    }

    @Override
    public Answer findAnswerToId(UUID answerID) {
        return answerRepository.findByAnswerId(answerID)
                .orElseThrow(() -> new CustomException(Error.ANSWER_NOT_FOUND));
    }

    @Override
    public boolean checkCorrectAnswer(UUID answerId) {
        Answer answer = answerRepository.findByAnswerId(answerId)
                .orElseThrow(() -> new CustomException(Error.ANSWER_NOT_FOUND));
        return answer.isCorrectAnswer();
    }

    @Override
    public int scoreAnswer(UUID answerId) {
        Answer answer = answerRepository.findByAnswerId(answerId)
                .orElseThrow(() -> new CustomException(Error.ANSWER_NOT_FOUND));
        return answer.getQuestion().getQuestionScore();
    }

    @Override
    public void deleteAnswer(Answer answer) {
        answerRepository.delete(answer);
    }

    @Override
    public Answer correctAnswer(Question question) {
        return answerRepository.findByQuestionAndCorrectAnswer(question, true).orElseThrow(() -> new CustomException(Error.ANSWER_BY_CORRECT_QUESTION_NOT_FOUND));
    }

    @Override
    public Answer choiceAnswer(Question question, MockTest mockTest) {
        for (DetailMockTest detailMockTest : detailMockTestRepository.findAllByMockTest(mockTest)) {
            if (detailMockTest.getAnswer().getQuestion().equals(question)) {
                return detailMockTest.getAnswer();
            }
        }
        return null;
    }

}
