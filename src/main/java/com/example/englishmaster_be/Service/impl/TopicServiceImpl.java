package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.DTO.Topic.UpdateTopicDTO;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
@Service
public class TopicServiceImpl implements ITopicService {
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private PartRepository partRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private IUserService IUserService;
    @Autowired
    private IQuestionService IQuestionService;


    @Override
    public void createTopic(Topic topic) {
        topicRepository.save(topic);
    }

    @Override
    public Topic findTopicById(UUID topicId) {
        return topicRepository.findByTopicId(topicId)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found with ID: " + topicId));
    }

    @Override
    public List<Topic> getTop6Topic(int index) {
        Page<Topic> page = topicRepository.findAll(PageRequest.of(index, 6, Sort.by(Sort.Order.desc("updateAt"))));
        return page.getContent();
    }

    @Override
    public List<Part> getPartToTopic(UUID topicId) {
        Topic topic = topicRepository.findByTopicId(topicId)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found with ID: " + topicId));
        Page<Part> page = partRepository.findByTopics(topic, PageRequest.of(0, 7, Sort.by(Sort.Order.asc("partName"))));
        return page.getContent();
    }

    @Override
    public List<Question> getQuestionOfPartToTopic(UUID topicId, UUID partId) {
        Topic topic = topicRepository.findByTopicId(topicId)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found with ID: " + topicId));
        Part part = partRepository.findByPartId(partId)
                .orElseThrow(() -> new IllegalArgumentException("Part not found with ID: " + partId));
        List<Question> listQuestion = questionRepository.findByTopicsAndPart(topic, part);
        Collections.shuffle(listQuestion);
        return listQuestion;
    }

    @Override
    public void addPartToTopic(UUID topicId, UUID partId) {
        Topic topic = topicRepository.findByTopicId(topicId)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found with ID: " + topicId));
        Part part = partRepository.findByPartId(partId)
                .orElseThrow(() -> new IllegalArgumentException("Part not found with ID: " + partId));

        topic.getParts().add(part);
        topicRepository.save(topic);
    }

    @Override
    public boolean deletePartToTopic(UUID topicId, UUID partId) {
        Topic topic = topicRepository.findByTopicId(topicId)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found with ID: " + topicId));
        Part part = partRepository.findByPartId(partId)
                .orElseThrow(() -> new IllegalArgumentException("Part not found with ID: " + partId));

        for (Part partTopic : topic.getParts()){
            if (partTopic.equals(part)){
                topic.getParts().remove(part);
                topicRepository.save(topic);
                return true;
            }
        }
        return false;
    }

    @Override
    public void addQuestionToTopic(Topic topic, Question question) {
        topic.getQuestions().add(question);
        topicRepository.save(topic);
    }

    @Override
    public void deleteQuestionToTopic(Topic topic, Question question) {
        topic.getQuestions().remove(question);
        topicRepository.save(topic);
    }

    @Override
    public boolean existQuestionInTopic(Topic topic, Question question) {
        for(Question questionItem : topic.getQuestions()){
            if(questionItem.equals(question)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean existPartInTopic(Topic topic, Part part) {
        for(Part partItem : topic.getParts()){
            if(partItem.equals(part)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateTopic(Topic topic, UpdateTopicDTO updateTopicDTO) {
        topic.setTopicName(updateTopicDTO.getTopicName());
        topic.setTopicDescription(updateTopicDTO.getTopicDiscription());
        topic.setTopicType(updateTopicDTO.getTopicType());
        topic.setWorkTime(updateTopicDTO.getWorkTime());
        topic.setStartTime(updateTopicDTO.getStartTime());
        topic.setEndTime(updateTopicDTO.getEndTime());
        topic.setEnable(updateTopicDTO.isEnable());
        topic.setUpdateAt(LocalDateTime.now());

        User user = IUserService.currentUser();
        topic.setUserUpdate(user);

        topicRepository.save(topic);
    }

    @Override
    public void deleteTopic(Topic topic) {
        topicRepository.delete(topic);
    }

    @Override
    public int totalQuestion(Part part, UUID topicId) {
        int total = 0;
        Topic topic = topicRepository.findByTopicId(topicId)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found with ID: " + topicId));

        for(Question question: topic.getQuestions()){
            if(question.getPart().getPartId() == part.getPartId()){
                boolean check = IQuestionService.checkQuestionGroup(question);
                if(check){
                    total = total + IQuestionService.countQuestionToQuestionGroup(question);
                }else {
                    total++;
                }
            }
        }
        return total;
    }
}
