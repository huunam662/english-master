package com.example.englishmaster_be.domain.topic.service;

import com.example.englishmaster_be.common.dto.response.FilterResponse;
import com.example.englishmaster_be.domain.question.dto.request.QuestionRequest;
import com.example.englishmaster_be.domain.topic.dto.request.TopicQuestionListRequest;
import com.example.englishmaster_be.domain.topic.dto.request.TopicRequest;
import com.example.englishmaster_be.domain.topic.dto.request.TopicFilterRequest;
import com.example.englishmaster_be.domain.topic.dto.request.TopicUploadFileRequest;
import com.example.englishmaster_be.domain.part.dto.response.PartResponse;
import com.example.englishmaster_be.domain.question.dto.response.QuestionResponse;
import com.example.englishmaster_be.model.comment.CommentEntity;
import com.example.englishmaster_be.model.pack.PackEntity;
import com.example.englishmaster_be.model.part.PartEntity;
import com.example.englishmaster_be.model.question.QuestionEntity;
import com.example.englishmaster_be.model.topic.TopicEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


public interface ITopicService {

    TopicEntity saveTopic(TopicRequest topicRequest);

    TopicEntity saveTopicByExcelFile(MultipartFile file, String url);

    TopicEntity updateTopicByExcelFile(@PathVariable UUID topicId, MultipartFile file, String url);

    TopicEntity uploadFileImage(UUID topicId, MultipartFile contentData);

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

    void addListQuestionToTopic(UUID topicId, TopicQuestionListRequest createQuestionDTOList);

    QuestionResponse addQuestionToTopic(UUID topicId, QuestionRequest createQuestionDTO);
}