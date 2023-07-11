package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.ITopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;
@Service
public class TopicServiceImpl implements ITopicService {
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private PartRepository partRepository;
    @Override
    public void createTopic(Topic topic) {
        topicRepository.save(topic);
    }

    @Override
    public Topic findTopicById(UUID topicId) {
        Topic topic = topicRepository.findByTopicId(topicId)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found with ID: " + topicId));
        return topic;
    }

    @Override
    public List<Topic> getTop6Topic(int index) {
        Page<Topic> page = topicRepository.findAll(PageRequest.of(index, 6, Sort.by(Sort.Order.desc("updateAt"))));
        return page.getContent();
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
}
