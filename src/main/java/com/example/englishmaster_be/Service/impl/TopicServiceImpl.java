package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Configuration.global.thread.MessageResponseHolder;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.ResponseModel;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.*;
import com.example.englishmaster_be.Util.LinkUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<Topic> get5TopicName(String keyword) {
        return topicRepository.findTopicsByQuery(keyword, PageRequest.of(0, 5, Sort.by(Sort.Order.asc("topicName").ignoreCase())));

    }

    @Override
    public List<Topic> getAllTopicToPack(Pack pack) {
        return topicRepository.findAllByPack(pack);
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

        if (topic.getParts() == null) {
            topic.setParts(new ArrayList<>());
        }
        topic.getParts().add(part);
        topicRepository.save(topic);
    }

    @Override
    public boolean deletePartToTopic(UUID topicId, UUID partId) {
        Topic topic = topicRepository.findByTopicId(topicId)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found with ID: " + topicId));
        Part part = partRepository.findByPartId(partId)
                .orElseThrow(() -> new IllegalArgumentException("Part not found with ID: " + partId));

        for (Part partTopic : topic.getParts()) {
            if (partTopic.equals(part)) {
                topic.getParts().remove(part);
                topicRepository.save(topic);
                return true;
            }
        }
        return false;
    }

    @Transactional
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
        for (Question questionItem : topic.getQuestions()) {
            if (questionItem.equals(question)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean existPartInTopic(Topic topic, Part part) {
        for (Part partItem : topic.getParts()) {
            if (partItem.equals(part)) {
                return true;
            }
        }
        return false;
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

        for (Question question : topic.getQuestions()) {
            if (question.getPart().getPartId() == part.getPartId()) {
                boolean check = IQuestionService.checkQuestionGroup(question);
                if (check) {
                    total = total + IQuestionService.countQuestionToQuestionGroup(question);
                } else {
                    total++;
                }
            }
        }
        return total;
    }


    @Override
    public List<Topic> getTopicsByStartTime(LocalDateTime startTime) {
        return topicRepository.findByStartTime(startTime);
    }


    @Override
    public List<String> getImageCdnLinkTopic() {

        List<String> listLinkCdn = topicRepository.findAllTopicImages();

        MessageResponseHolder.setMessage("Found " + listLinkCdn.size() + " links");

        return listLinkCdn.stream()
                .filter(linkCdn -> linkCdn != null && !linkCdn.isEmpty())
                .map(linkCdn -> LinkUtil.linkFileShowImageBE + linkCdn)
                .toList();
    }
}
