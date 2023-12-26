package com.example.englishmaster_be.Service.impl;

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

    @Override
    public boolean existQuestion(Answer answer, Question question) {
        return answer.getQuestion().equals(question);
    }

    @Override
    public Answer findAnswerToId(UUID answerID) {
        return answerRepository.findByAnswerId(answerID)
                .orElseThrow(() -> new IllegalArgumentException("Answer not found with ID: " + answerID));
    }

    @Override
    public boolean checkCorrectAnswer(UUID answerId) {
        Answer answer = answerRepository.findByAnswerId(answerId)
                .orElseThrow(() -> new IllegalArgumentException("Answer not found with ID: " + answerId));
        return answer.isCorrectAnswer();
    }

    @Override
    public int scoreAnswer(UUID answerId) {
        Answer answer = answerRepository.findByAnswerId(answerId)
                .orElseThrow(() -> new IllegalArgumentException("Answer not found with ID: " + answerId));
        return answer.getQuestion().getQuestionScore();
    }

    @Override
    public void deleteAnswer(Answer answer) {
        answerRepository.delete(answer);
    }

    @Override
    public Answer correctAnswer(Question question) {
        return answerRepository.findByQuestionAndCorrectAnswer(question, true).orElseThrow(() -> new IllegalArgumentException("Question don't have correct answer"));
    }

    @Override
    public Answer choiceAnswer(Question question, MockTest mockTest) {
        for(DetailMockTest detailMockTest: detailMockTestRepository.findAllByMockTest(mockTest)){
            if(detailMockTest.getAnswer().getQuestion().equals(question)){
                return detailMockTest.getAnswer();
            }
        }
        return null;
    }

}
