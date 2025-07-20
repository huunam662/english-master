package com.example.englishmaster_be.domain.exam.answer.service;

import com.example.englishmaster_be.advice.exception.ApplicationException;
import com.example.englishmaster_be.domain.exam.answer.dto.req.AnswerReq;
import com.example.englishmaster_be.domain.exam.answer.mapper.AnswerMapper;
import com.example.englishmaster_be.domain.exam.answer.repository.AnswerRepository;
import com.example.englishmaster_be.domain.exam.answer.model.AnswerEntity;
import com.example.englishmaster_be.domain.exam.question.model.QuestionEntity;
import com.example.englishmaster_be.domain.exam.question.service.IQuestionService;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class AnswerService implements IAnswerService {

    private final AnswerRepository answerRepository;
    private final IQuestionService questionService;

    @Lazy
    public AnswerService(AnswerRepository answerRepository, IQuestionService questionService) {
        this.answerRepository = answerRepository;
        this.questionService = questionService;
    }

    @Transactional
    @Override
    public AnswerEntity saveAnswer(AnswerReq answerRequest) {

        QuestionEntity question = questionService.getQuestionById(answerRequest.getQuestionId());

        AnswerEntity answer;

        if(answerRequest.getAnswerId() != null)
            answer = getAnswerById(answerRequest.getAnswerId());
        else answer = new AnswerEntity();

        answer.setQuestion(question);

        AnswerMapper.INSTANCE.flowToAnswerEntity(answerRequest, answer);

        return answerRepository.save(answer);
    }

    @Override
    public boolean existQuestion(AnswerEntity answer, QuestionEntity question) {
        return answer.getQuestion().equals(question);
    }

    @Override
    public AnswerEntity getAnswerById(UUID answerID) {

        return answerRepository.findByAnswerId(answerID)
                .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "Answer not found."));
    }

    @Transactional
    @Override
    public void deleteAnswer(UUID answerId) {

        AnswerEntity answer = getAnswerById(answerId);

        answerRepository.delete(answer);
    }

    @Override
    public AnswerEntity correctAnswer(QuestionEntity question) {

        return answerRepository.findByQuestionAndCorrectAnswer(question, Boolean.TRUE)
                .orElseThrow(
                        () -> new ApplicationException(HttpStatus.NOT_FOUND, "Answer not found.")
                );
    }

    @Override
    public List<AnswerEntity> getListAnswerByQuestionId(UUID questionId) {

        QuestionEntity question = questionService.getQuestionById(questionId);

        return question.getAnswers().stream().toList();
    }

    @Transactional
    @Override
    public AnswerEntity save(AnswerEntity answer) {

        return answerRepository.save(answer);
    }

    @Transactional
    @Override
    public List<AnswerEntity> saveAll(Set<AnswerEntity> answers) {
        return answerRepository.saveAll(answers);
    }
}
