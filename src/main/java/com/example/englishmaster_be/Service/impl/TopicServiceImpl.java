package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Model.Topic;
import com.example.englishmaster_be.Repository.TopicRepository;
import com.example.englishmaster_be.Service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicServiceImpl implements TopicService {
    @Autowired
    private TopicRepository topicRepository;
    @Override
    public void createTopic(Topic topic) {
        topicRepository.save(topic);
    }

    @Override
    public List<Topic> getTop6Topic(int index) {
        Page<Topic> page = topicRepository.findAll(PageRequest.of(index, 6, Sort.by(Sort.Order.desc("updateAt"))));
        return page.getContent();
    }
}
