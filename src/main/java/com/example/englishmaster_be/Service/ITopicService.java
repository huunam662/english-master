package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Model.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


public interface ITopicService {
    void createTopic(Topic topic);

    Topic findTopicById(UUID topicId);

    List<Topic> getTop6Topic(int index);

    void addPartToTopic(UUID topicId, UUID partId);
    boolean deletePartToTopic(UUID topicId, UUID partId);

    void addQuestionToTopic(Topic topic, Question question);
    void deleteQuestionToTopic(Topic topic, Question question);
    boolean existQuestionInTopic(Topic topic, Question question);
    boolean existPartInTopic(Topic topic, Part part);

}
