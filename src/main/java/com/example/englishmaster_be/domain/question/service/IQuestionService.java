package com.example.englishmaster_be.domain.question.service;

import com.example.englishmaster_be.domain.part.dto.response.PartKeyResponse;
import com.example.englishmaster_be.domain.part.dto.response.PartQuestionResponse;
import com.example.englishmaster_be.domain.question.dto.request.QuestionGroupRequest;
import com.example.englishmaster_be.domain.question.dto.request.QuestionParentRequest;
import com.example.englishmaster_be.domain.question.dto.request.QuestionRequest;
import com.example.englishmaster_be.domain.question.dto.request.QuestionUpdateRequest;
import com.example.englishmaster_be.domain.question.dto.response.QuestionDto;
import com.example.englishmaster_be.domain.question.dto.response.QuestionPartResponse;
import com.example.englishmaster_be.model.part.PartEntity;
import com.example.englishmaster_be.model.question.QuestionEntity;
import com.example.englishmaster_be.model.topic.TopicEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.UUID;


public interface IQuestionService {

    QuestionEntity saveQuestion(QuestionRequest questionRequest);

    QuestionEntity updateQuestion(QuestionUpdateRequest questionUpdateRequest);

    QuestionEntity getQuestionById(UUID questionId);

    QuestionEntity uploadFileQuestion(UUID questionId, List<MultipartFile> newFile);

    QuestionEntity updateFileQuestion(UUID questionId, String oldFileName, MultipartFile newFile);

    QuestionEntity createGroupQuestion(QuestionGroupRequest createGroupQuestionDTO);

    List<QuestionEntity> getTop10Question(int index, UUID partId);

    int countQuestionToQuestionGroup(QuestionEntity question);

    boolean checkQuestionGroup(UUID questionId);

    List<QuestionEntity> getQuestionsParentBy(List<PartEntity> partEntityList, TopicEntity topicEntity);

    List<QuestionEntity> listQuestionGroup(QuestionEntity question);

    void deleteQuestion(UUID questionId);

    List<QuestionEntity> getQuestionGroupListByQuestionId(UUID questionId);

    List<QuestionPartResponse> getAllPartQuestions(String partName, UUID topicId);

    void createListQuestionsParentOfPart(PartEntity part, List<QuestionParentRequest> questionParentsRequest);

}
