package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Common.dto.response.FilterResponse;
import com.example.englishmaster_be.DTO.Question.SaveQuestionDTO;
import com.example.englishmaster_be.DTO.Topic.SaveListQuestionDTO;
import com.example.englishmaster_be.DTO.Topic.SaveTopicDTO;
import com.example.englishmaster_be.DTO.Topic.TopicFilterRequest;
import com.example.englishmaster_be.DTO.UploadFileDTO;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.CommentResponse;
import com.example.englishmaster_be.Model.Response.PartResponse;
import com.example.englishmaster_be.Model.Response.QuestionResponse;
import com.example.englishmaster_be.Model.Response.TopicResponse;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public interface ITopicService {

    TopicResponse saveTopic(SaveTopicDTO saveTopicDTO);

    TopicResponse saveTopicByExcelFile(MultipartFile file, String url);

    TopicResponse uploadFileImage(UUID topicId, UploadFileDTO uploadFileDTO);

    Topic findTopicById(UUID topicId);

    List<Topic> get5TopicName(String query);

    List<Topic> getAllTopicToPack(Pack pack);

    List<PartResponse> getPartToTopic(UUID topicId);

    List<Question> getQuestionOfPartToTopic(UUID topicId, UUID partId);

    FilterResponse<?> getAllTopic(TopicFilterRequest filterRequest);

    void addPartToTopic(UUID topicId, UUID partId);

    void deletePartToTopic(UUID topicId, UUID partId);

    boolean existQuestionInTopic(Topic topic, Question question);

    boolean existPartInTopic(Topic topic, Part part);

    void deleteTopic(UUID topicId);

    int totalQuestion(Part part, UUID topicId);

    List<TopicResponse> getTopicsByStartTime(LocalDateTime startTime);

    List<String> getImageCdnLinkTopic();

    List<CommentResponse> listComment(UUID topicId);

    void enableTopic(UUID topicId, boolean enable);

    List<QuestionResponse> getQuestionOfToTopic(UUID topicId, UUID partId);

    List<String> get5SuggestTopic(String query);

    TopicResponse getTopic(UUID id);

    void deleteQuestionToTopic(UUID topicId, UUID questionId);

    void addAllPartsToTopicByExcelFile(UUID topicId, MultipartFile file);

    void addListQuestionPart123467ToTopicByExcelFile(UUID topicId, MultipartFile file, int partName);

    void addListQuestionPart5ToTopicByExcelFile(UUID topicId, MultipartFile file);

    void addListQuestionToTopic(UUID topicId, SaveListQuestionDTO createQuestionDTOList);

    QuestionResponse addQuestionToTopic(UUID topicId, SaveQuestionDTO createQuestionDTO);
}