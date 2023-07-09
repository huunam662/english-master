package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Model.Part;
import com.example.englishmaster_be.Model.Question;
import com.example.englishmaster_be.Repository.PartRepository;
import com.example.englishmaster_be.Repository.QuestionRepository;
import com.example.englishmaster_be.Service.IQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class QuestionServiceImpl implements IQuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private PartRepository partRepository;
    @Override
    public void createQuestion(Question question) {
        questionRepository.save(question);
    }

    @Override
    public Question findQuestionById(UUID questionId) {
        Question question = questionRepository.findByQuestionId(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found with ID: " + questionId));
        return question;
    }

    @Override
    public void uploadFileQuestion(Question question) {
        questionRepository.save(question);
    }

    @Override
    public List<Question> getTop10Question(int index, UUID partId) {
        Part part = partRepository.findByPartId(partId)
                .orElseThrow(() -> new IllegalArgumentException("Part not found with ID: " + partId));

        Page<Question> questionPage= questionRepository.findAllByQuestionGroupAndPart(null,part,PageRequest.of(index, 10, Sort.by(Sort.Order.desc("updateAt"))));

        return questionPage.getContent();
    }

    @Override
    public int countQuestionToQuestionGroup(Question question) {
        return questionRepository.countByQuestionGroup(question);
    }

    @Override
    public boolean checkQuestionGroup(Question question) {
        return questionRepository.existsByQuestionGroup(question);
    }

    @Override
    public List<Question> listQuestionGroup(Question question) {
        List<Question> questionList = questionRepository.findAllByQuestionGroup(question);
        return questionList;
    }
}
