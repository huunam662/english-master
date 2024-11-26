package com.example.englishmaster_be.service.impl;

import com.example.englishmaster_be.model.Answer;
import com.example.englishmaster_be.model.Part;
import com.example.englishmaster_be.model.Question;
import com.example.englishmaster_be.repository.PartRepository;
import com.example.englishmaster_be.repository.QuestionRepository;
import com.example.englishmaster_be.service.IQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class QuestionServiceImpl implements IQuestionService {

    @Autowired
    private QuestionRepository questionRepository;


    @Autowired
    private PartRepository partRepository;

    @Transactional
    @Override
    public void createQuestion(Question question) {
        questionRepository.save(question);
    }

    @Override
    public Question findQuestionById(UUID questionId) {
        return questionRepository.findByQuestionId(questionId)
                .orElseThrow(() -> new IllegalArgumentException("question not found with ID: " + questionId));
    }

    @Override
    public void uploadFileQuestion(Question question) {
        questionRepository.save(question);
    }

    @Override
    public List<Question> getTop10Question(int index, UUID partId) {
        Part part = partRepository.findByPartId(partId)
                .orElseThrow(() -> new IllegalArgumentException("part not found with ID: " + partId));

        Page<Question> questionPage= questionRepository.findAllByQuestionGroupAndPart(null,part,PageRequest.of(index, 10, Sort.by(Sort.Order.desc("updateAt"))));

        return questionPage.getContent();
    }

    @Override
    public int countQuestionToQuestionGroup(Question question) {
        int total = 0;
        for(Question questionChild: listQuestionGroup(question)){
            total++;
        }
        return total;
    }

    @Override
    public boolean checkQuestionGroup(Question question) {
        return questionRepository.existsByQuestionGroup(question);
    }

    @Override
    public List<Question> listQuestionGroup(Question question) {
        return questionRepository.findAllByQuestionGroup(question);
    }

    @Override
    public void deleteQuestion(Question question) {
        questionRepository.delete(question);
    }


}
