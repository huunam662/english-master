package com.example.englishmaster_be.domain.question.contorller;


import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.domain.answer.service.IAnswerService;
import com.example.englishmaster_be.domain.question.service.IQuestionService;
import com.example.englishmaster_be.mapper.AnswerMapper;
import com.example.englishmaster_be.mapper.QuestionMapper;
import com.example.englishmaster_be.domain.question.dto.request.QuestionGroupRequest;
import com.example.englishmaster_be.domain.question.dto.request.QuestionRequest;
import com.example.englishmaster_be.shared.dto.request.upload_file.UploadMultipleFileRequest;
import com.example.englishmaster_be.domain.answer.dto.response.AnswerResponse;
import com.example.englishmaster_be.domain.question.dto.response.QuestionBasicResponse;
import com.example.englishmaster_be.domain.question.dto.response.QuestionResponse;
import com.example.englishmaster_be.model.answer.AnswerEntity;
import com.example.englishmaster_be.model.question.QuestionEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Tag(name = "Question")
@RestController
@RequestMapping("/question")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionController {

    IQuestionService questionService;

    IAnswerService answerService;


    @PostMapping(value = "/create")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Create QuestionEntity successfully")
    public QuestionResponse createQuestion(
            @ModelAttribute QuestionRequest questionRequest
    ) {

        QuestionEntity question = questionService.saveQuestion(questionRequest);

        return QuestionMapper.INSTANCE.toQuestionResponse(question);
    }

    @PutMapping(value = "/{questionId:.+}/editQuestion")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Update QuestionEntity to TopicEntity successfully")
    public QuestionResponse editQuestion(
            @PathVariable UUID questionId,
            @ModelAttribute QuestionRequest questionRequest
    ) {

        questionRequest.setQuestionId(questionId);

        QuestionEntity question = questionService.saveQuestion(questionRequest);

        return QuestionMapper.INSTANCE.toQuestionResponse(question);
    }


    @PutMapping(value = "/{questionId:.+}/uploadfile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Upload QuestionEntity successfully")
    public QuestionBasicResponse uploadFileQuestion(
            @PathVariable UUID questionId,
            @ModelAttribute UploadMultipleFileRequest uploadMultiFileRequest
    ) {

        QuestionEntity question = questionService.uploadFileQuestion(questionId, uploadMultiFileRequest);

        return QuestionMapper.INSTANCE.toQuestionBasicResponse(question);
    }

    @PutMapping(value = "/{questionId:.+}/updatefile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Update file_storage QuestionEntity successfully")
    public QuestionBasicResponse updateFileQuestion(
            @PathVariable UUID questionId,
            @RequestParam("oldFileName") String oldFileName,
            @RequestPart MultipartFile newFile
    ) {

        QuestionEntity question = questionService.updateFileQuestion(questionId, oldFileName, newFile);

        return QuestionMapper.INSTANCE.toQuestionBasicResponse(question);
    }

    @PostMapping(value = "/create/groupQuestion")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Create QuestionEntity successfully")
    public QuestionResponse createGroupQuestion(
            @RequestBody QuestionGroupRequest groupQuestionRequest
    ) {

        QuestionEntity question = questionService.createGroupQuestion(groupQuestionRequest);

        return QuestionMapper.INSTANCE.toQuestionResponse(question);
    }


    @GetMapping(value = "/{partId:.+}/listTop10Question")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("List top 10 QuestionEntity successfully")
    public List<QuestionBasicResponse> getTop10Question(
            @PathVariable UUID partId,
            @RequestParam int indexp
    ) {

        List<QuestionEntity> questionList = questionService.getTop10Question(indexp, partId);

        return QuestionMapper.INSTANCE.toQuestionBasicResponseList(questionList);
    }

    @GetMapping(value = "/{questionId:.+}/checkQuestionGroup")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("QuestionEntity has QuestionEntity group")
    public void checkQuestionGroup(@PathVariable UUID questionId) {

        questionService.checkQuestionGroup(questionId);
    }

    @GetMapping(value = "/{questionId:.+}/listQuestionGroup")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("List QuestionEntity group successfully")
    public List<QuestionResponse> getQuestionGroupToQuestion(@PathVariable UUID questionId) {

        List<QuestionEntity> questionEntityList = questionService.getQuestionGroupListByQuestionId(questionId);

        return QuestionMapper.INSTANCE.toQuestionGroupChildrenResponseList(questionEntityList);
    }

    @GetMapping(value = "/{questionId:.+}/listAnswer")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("List AnswerEntity to QuestionEntity successfully")
    public List<AnswerResponse> getAnswerToQuestion(@PathVariable UUID questionId) {

        List<AnswerEntity> answerList = answerService.getListAnswerByQuestionId(questionId);

        return AnswerMapper.INSTANCE.toAnswerResponseList(answerList);
    }

    @DeleteMapping(value = "/{questionId:.+}/deleteQuestion")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Delete QuestionEntity successfully")
    public void deleteQuestion(@PathVariable UUID questionId) {

        questionService.deleteQuestion(questionId);
    }


    @GetMapping(value = "/{questionId:.+}/content")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Show ContentEntity QuestionEntity successfully")
    public QuestionResponse getContentToQuestion(@PathVariable UUID questionId) {

        QuestionEntity question = questionService.getQuestionById(questionId);

        return QuestionMapper.INSTANCE.toQuestionResponse(question);
    }

}
