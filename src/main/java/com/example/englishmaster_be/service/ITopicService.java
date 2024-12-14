package com.example.englishmaster_be.service;

import com.example.englishmaster_be.common.dto.response.FilterResponse;
import com.example.englishmaster_be.model.request.Question.QuestionRequest;
import com.example.englishmaster_be.model.request.Topic.ListQuestionRequest;
import com.example.englishmaster_be.model.request.Topic.TopicRequest;
import com.example.englishmaster_be.model.request.Topic.TopicFilterRequest;
import com.example.englishmaster_be.model.request.UploadFileRequest;
import com.example.englishmaster_be.model.response.CommentResponse;
import com.example.englishmaster_be.model.response.PartResponse;
import com.example.englishmaster_be.model.response.QuestionResponse;
import com.example.englishmaster_be.model.response.TopicResponse;
import com.example.englishmaster_be.entity.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


public interface ITopicService {

    TopicEntity saveTopic(TopicRequest topicRequest);

    TopicEntity saveTopicByExcelFile(MultipartFile file, String url);

    TopicEntity updateTopicByExcelFile(@PathVariable UUID topicId, MultipartFile file, String url);

    TopicEntity uploadFileImage(UUID topicId, UploadFileRequest uploadFileRequest);

    TopicEntity getTopicById(UUID topicId);

    List<TopicEntity> get5TopicName(String query);

    List<TopicEntity> getAllTopicToPack(PackEntity pack);

    List<PartResponse> getPartToTopic(UUID topicId);

    List<QuestionEntity> getQuestionOfPartToTopic(UUID topicId, UUID partId);

    FilterResponse<?> getAllTopic(TopicFilterRequest filterRequest);

    void addPartToTopic(UUID topicId, UUID partId);

    void deletePartToTopic(UUID topicId, UUID partId);

    boolean existQuestionInTopic(TopicEntity topic, QuestionEntity question);

    boolean existPartInTopic(TopicEntity topic, PartEntity part);

    void deleteTopic(UUID topicId);

    int totalQuestion(PartEntity part, UUID topicId);

    List<TopicEntity> getTopicsByStartTime(LocalDateTime startTime);

    List<String> getImageCdnLinkTopic();

    List<CommentEntity> listComment(UUID topicId);

    void enableTopic(UUID topicId, boolean enable);

    List<QuestionResponse> getQuestionOfToTopic(UUID topicId, UUID partId);

    List<String> get5SuggestTopic(String query);

    void deleteQuestionToTopic(UUID topicId, UUID questionId);

    void addAllPartsToTopicByExcelFile(UUID topicId, MultipartFile file);

    void addListQuestionPart123467ToTopicByExcelFile(UUID topicId, MultipartFile file, int partName);

    void addListQuestionPart5ToTopicByExcelFile(UUID topicId, MultipartFile file);

    void addListQuestionToTopic(UUID topicId, ListQuestionRequest createQuestionDTOList);

    QuestionResponse addQuestionToTopic(UUID topicId, QuestionRequest createQuestionDTO);
}