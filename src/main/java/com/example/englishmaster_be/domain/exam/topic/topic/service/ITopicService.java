package com.example.englishmaster_be.domain.exam.topic.topic.service;

import com.example.englishmaster_be.common.dto.req.PageOptionsReq;
import com.example.englishmaster_be.domain.exam.topic.topic.dto.view.ITopicPageView;
import com.example.englishmaster_be.domain.exam.topic.topic.dto.res.TopicKeyRes;
import com.example.englishmaster_be.domain.exam.question.dto.res.QuestionPartRes;
import com.example.englishmaster_be.domain.exam.topic.topic.dto.req.TopicReq;
import com.example.englishmaster_be.domain.exam.part.dto.res.PartRes;
import com.example.englishmaster_be.domain.exam.pack.pack.model.PackEntity;
import com.example.englishmaster_be.domain.exam.question.model.QuestionEntity;
import com.example.englishmaster_be.domain.exam.topic.topic.model.TopicEntity;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;


public interface ITopicService {

    TopicEntity createTopic(TopicReq topicRequest);

    TopicEntity updateTopic(UUID topicId, TopicReq topicRequest);

    TopicEntity getTopicById(UUID topicId);

    List<TopicEntity> get5TopicName(String query);

    List<TopicEntity> getAllTopicToPack(PackEntity pack);

    List<PartRes> getPartToTopic(UUID topicId);

    List<QuestionEntity> getQuestionOfPartToTopic(UUID topicId, UUID partId);

    Page<ITopicPageView> getPageTopics(PageOptionsReq optionsReq);

    Page<ITopicPageView> getPageTopicsToPack(UUID packId, PageOptionsReq optionsReq);

    void addPartToTopic(UUID topicId, UUID partId);

    void deletePartToTopic(UUID topicId, UUID partId);

    void deleteTopic(UUID topicId);

    void enableTopic(UUID topicId, boolean enable);

    List<QuestionPartRes> getQuestionOfToTopicPart(UUID topicId, String partName);

    List<QuestionPartRes> getQuestionOfToTopicPart(UUID topicId, UUID partId);

    List<String> get5SuggestTopic(String query);

    List<QuestionPartRes> getQuestionPartListOfTopic(UUID topicId);

    TopicKeyRes updateTopicToExcel(MultipartFile file, UUID topicId, String imageUrl, String audioUrl) throws BadRequestException;

}