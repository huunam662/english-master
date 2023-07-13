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

    @Override
    public void createAnswer(Answer answer) {
        answerRepository.save(answer);
    }

    @Override
    public boolean existQuestion(Answer answer, Question question) {
        if(answer.getQuestion().equals(question)){
            return true;
        }
        return false;
    }

    @Override
    public Answer findAnswerToId(UUID answerID) {
        return answerRepository.findByAnswerId(answerID);
    }

    @Override
    public void deleteAnswer(Answer answer) {
        answerRepository.delete(answer);
    }

}
