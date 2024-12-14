package com.example.englishmaster_be.service;

import com.example.englishmaster_be.model.request.Question.GroupQuestionRequest;
import com.example.englishmaster_be.model.request.Question.QuestionRequest;
import com.example.englishmaster_be.model.request.UploadMultiFileRequest;
import com.example.englishmaster_be.model.response.QuestionGroupResponse;
import com.example.englishmaster_be.model.response.QuestionResponse;
import com.example.englishmaster_be.entity.QuestionEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;


public interface IQuestionService {

    QuestionEntity saveQuestion(QuestionRequest questionRequest);

    QuestionEntity getQuestionById(UUID questionId);

    QuestionEntity uploadFileQuestion(UUID questionId, UploadMultiFileRequest uploadMultiFileDTO);

    QuestionEntity updateFileQuestion(UUID questionId, String oldFileName, MultipartFile newFile);

    QuestionEntity createGroupQuestion(GroupQuestionRequest createGroupQuestionDTO);

    List<QuestionEntity> getTop10Question(int index, UUID partId);

    int countQuestionToQuestionGroup(QuestionEntity question);

    boolean checkQuestionGroup(UUID questionId);

    List<QuestionEntity> listQuestionGroup(QuestionEntity question);

    void deleteQuestion(UUID questionId);

    List<QuestionEntity> getQuestionGroupListByQuestionId(UUID questionId);

}
