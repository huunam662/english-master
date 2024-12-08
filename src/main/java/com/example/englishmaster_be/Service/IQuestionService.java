package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.DTO.Question.SaveGroupQuestionDTO;
import com.example.englishmaster_be.DTO.Question.SaveQuestionDTO;
import com.example.englishmaster_be.DTO.UploadFileDTO;
import com.example.englishmaster_be.DTO.UploadMultiFileDTO;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.QuestionGroupResponse;
import com.example.englishmaster_be.Model.Response.QuestionResponse;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;


public interface IQuestionService {

    QuestionResponse saveQuestion(SaveQuestionDTO saveQuestionDTO);

    Question getQuestionById(UUID questionId);

    QuestionResponse uploadFileQuestion(UUID questionId, UploadMultiFileDTO uploadMultiFileDTO);

    QuestionResponse updateFileQuestion(UUID questionId, String oldFileName, MultipartFile newFile);

    QuestionResponse createGroupQuestion(SaveGroupQuestionDTO createGroupQuestionDTO);

    List<Question> getTop10Question(int index, UUID partId);

    int countQuestionToQuestionGroup(Question question);

    boolean checkQuestionGroup(UUID questionId);

    List<Question> listQuestionGroup(Question question);

    void deleteQuestion(UUID questionId);

    List<QuestionGroupResponse> getQuestionGroupToQuestion(UUID questionId);

}
