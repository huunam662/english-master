package com.example.englishmaster_be.domain.question.service;

import com.example.englishmaster_be.domain.question.dto.request.QuestionGroupRequest;
import com.example.englishmaster_be.domain.question.dto.request.QuestionRequest;
import com.example.englishmaster_be.domain.question.dto.response.QuestionFromPartResponse;
import com.example.englishmaster_be.model.question.QuestionEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;


public interface IQuestionService {

    QuestionEntity saveQuestion(QuestionRequest questionRequest);

    QuestionEntity getQuestionById(UUID questionId);

    QuestionEntity uploadFileQuestion(UUID questionId, List<MultipartFile> uploadMultiFileDTO);

    QuestionEntity updateFileQuestion(UUID questionId, String oldFileName, MultipartFile newFile);

    QuestionEntity createGroupQuestion(QuestionGroupRequest createGroupQuestionDTO);

    List<QuestionEntity> getTop10Question(int index, UUID partId);

    int countQuestionToQuestionGroup(QuestionEntity question);

    boolean checkQuestionGroup(UUID questionId);

    List<QuestionEntity> listQuestionGroup(QuestionEntity question);

    void deleteQuestion(UUID questionId);

    List<QuestionEntity> getQuestionGroupListByQuestionId(UUID questionId);

    List<QuestionFromPartResponse> getAllQuestionFromPart(UUID partId);
}
