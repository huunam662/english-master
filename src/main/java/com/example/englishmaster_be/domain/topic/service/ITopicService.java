package com.example.englishmaster_be.domain.topic.service;

import com.example.englishmaster_be.domain.topic.dto.projection.INumberAndScoreQuestionTopic;
import com.example.englishmaster_be.shared.dto.response.FilterResponse;
import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelQuestionListResponse;
import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelTopicResponse;
import com.example.englishmaster_be.domain.question.dto.request.QuestionRequest;
import com.example.englishmaster_be.domain.question.dto.response.QuestionPartResponse;
import com.example.englishmaster_be.domain.question.dto.response.QuestionResponse;
import com.example.englishmaster_be.domain.topic.dto.request.TopicQuestionListRequest;
import com.example.englishmaster_be.domain.topic.dto.request.TopicRequest;
import com.example.englishmaster_be.domain.topic.dto.request.TopicFilterRequest;
import com.example.englishmaster_be.domain.part.dto.response.PartResponse;
import com.example.englishmaster_be.domain.comment.model.CommentEntity;
import com.example.englishmaster_be.domain.pack.model.PackEntity;
import com.example.englishmaster_be.domain.part.model.PartEntity;
import com.example.englishmaster_be.domain.question.model.QuestionEntity;
import com.example.englishmaster_be.domain.topic.model.TopicEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


public interface ITopicService {

    TopicEntity createTopic(TopicRequest topicRequest);

    TopicEntity updateTopic(UUID topicId, TopicRequest topicRequest);

    ExcelTopicResponse saveTopicByExcelFile(MultipartFile file);

    ExcelTopicResponse updateTopicByExcelFile(@PathVariable UUID topicId, MultipartFile file);

    TopicEntity uploadFileImage(UUID topicId, MultipartFile contentData);

    TopicEntity getTopicById(UUID topicId);

    TopicEntity getTopicByName(String topicName);

    List<TopicEntity> get5TopicName(String query);

    List<TopicEntity> getAllTopicToPack(PackEntity pack);

    List<PartResponse> getPartToTopic(UUID topicId);

    List<QuestionEntity> getQuestionOfPartToTopic(UUID topicId, UUID partId);

    FilterResponse<?> filterTopics(TopicFilterRequest filterRequest);

    void addPartToTopic(UUID topicId, UUID partId);

    void deletePartToTopic(UUID topicId, UUID partId);

    boolean existQuestionInTopic(TopicEntity topic, QuestionEntity question);

    boolean existPartInTopic(TopicEntity topic, PartEntity part);

    void deleteTopic(UUID topicId);

    int totalQuestion(PartEntity part, UUID topicId);

    List<TopicEntity> getTopicsByStartTime(LocalDate startTime);

    List<String> getImageCdnLinkTopic();

    List<CommentEntity> listComment(UUID topicId);

    void enableTopic(UUID topicId, boolean enable);

    List<QuestionPartResponse> getQuestionOfToTopicPart(UUID topicId, String partName);

    List<QuestionPartResponse> getQuestionOfToTopicPart(UUID topicId, UUID partId);

    List<String> get5SuggestTopic(String query);

    void deleteQuestionToTopic(UUID topicId, UUID questionId);

    ExcelTopicResponse addAllPartsForTopicByExcelFile(UUID topicId, MultipartFile file);

    ExcelQuestionListResponse addQuestionAllPartsToTopicByExcelFile(UUID topicId, MultipartFile file);

    ExcelQuestionListResponse addQuestionForTopicAndPartByExcelFile(UUID topicId, int partNumber, MultipartFile file);

    void addListQuestionToTopic(UUID topicId, TopicQuestionListRequest createQuestionDTOList);

    QuestionResponse addQuestionToTopic(UUID topicId, QuestionRequest createQuestionDTO);

    List<QuestionPartResponse> getQuestionPartListOfTopic(UUID topicId);

    INumberAndScoreQuestionTopic getNumberAndScoreQuestionTopic(UUID topicId);

}