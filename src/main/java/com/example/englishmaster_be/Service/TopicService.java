package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Model.Topic;

import java.util.List;

public interface TopicService {
    void createTopic(Topic topic);

    List<Topic> getTop6Topic(int index);
}
