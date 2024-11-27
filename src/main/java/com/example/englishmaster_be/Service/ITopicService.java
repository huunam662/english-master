package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Model.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public interface ITopicService {
    void createTopic(Topic topic);
    Topic findTopicById(UUID topicId);
    List<Topic> get5TopicName(String query);
    List<Topic> getAllTopicToPack(Pack pack);
    List<Part> getPartToTopic(UUID topicId);
    List<Question> getQuestionOfPartToTopic(UUID topicId, UUID partId);
    void addPartToTopic(UUID topicId, UUID partId);
    boolean deletePartToTopic(UUID topicId, UUID partId);
    void addQuestionToTopic(Topic topic, Question question);
    void deleteQuestionToTopic(Topic topic, Question question);
    boolean existQuestionInTopic(Topic topic, Question question);
    boolean existPartInTopic(Topic topic, Part part);
    void deleteTopic(Topic topic);
    int totalQuestion(Part part, UUID topicId);
    List<Topic> getTopicsByStartTime(LocalDateTime startTime);
}