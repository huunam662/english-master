package com.example.englishmaster_be.domain.question.controller;


import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.domain.answer.service.IAnswerService;
import com.example.englishmaster_be.domain.question.dto.request.QuestionUpdateRequest;
import com.example.englishmaster_be.domain.question.dto.response.QuestionPartResponse;
import com.example.englishmaster_be.domain.question.service.IQuestionService;
import com.example.englishmaster_be.domain.answer.mapper.AnswerMapper;
import com.example.englishmaster_be.domain.question.mapper.QuestionMapper;
import com.example.englishmaster_be.domain.question.dto.request.QuestionGroupRequest;
import com.example.englishmaster_be.domain.question.dto.request.QuestionRequest;
import com.example.englishmaster_be.domain.answer.dto.response.AnswerResponse;
import com.example.englishmaster_be.domain.question.dto.response.QuestionResponse;
import com.example.englishmaster_be.domain.answer.model.AnswerEntity;
import com.example.englishmaster_be.domain.question.model.QuestionEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
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
    @DefaultMessage("Create question successfully")
    public QuestionResponse createQuestion(
            @RequestBody QuestionRequest questionRequest
    ) {

        QuestionEntity question = questionService.saveQuestion(questionRequest);
        return QuestionMapper.INSTANCE.toQuestionResponse(question);
    }

    @PutMapping(value = "/{questionId:.+}/editQuestion")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Update question to topic successfully")
    public QuestionResponse editQuestion(
            @PathVariable("questionId") UUID questionId,
            @RequestBody QuestionUpdateRequest questionRequest
    ) {

        questionRequest.setQuestionId(questionId);

        QuestionEntity question = questionService.updateQuestion(questionRequest);

        return QuestionMapper.INSTANCE.toQuestionResponse(question);
    }

    @PutMapping(value = "/{questionId:.+}/uploadfile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Upload question successfully")
    public QuestionResponse uploadFileQuestion(
            @PathVariable("questionId") UUID questionId,
            @RequestPart List<MultipartFile> uploadMultiFileRequest
    ) {

        QuestionEntity question = questionService.uploadFileQuestion(questionId, uploadMultiFileRequest);

        return QuestionMapper.INSTANCE.toQuestionResponse(question);
    }

    @PutMapping(value = "/{questionId:.+}/updatefile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Update file question successfully")
    public QuestionResponse updateFileQuestion(
            @PathVariable("questionId") UUID questionId,
            @RequestParam("oldFileName") String oldFileName,
            @RequestPart MultipartFile newFile
    ) {

        QuestionEntity question = questionService.updateFileQuestion(questionId, oldFileName, newFile);

        return QuestionMapper.INSTANCE.toQuestionResponse(question);
    }

    @PostMapping(value = "/create/groupQuestion")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Create question successfully")
    public QuestionResponse createGroupQuestion(
            @RequestBody QuestionGroupRequest groupQuestionRequest
    ) {

        QuestionEntity question = questionService.createGroupQuestion(groupQuestionRequest);

        return QuestionMapper.INSTANCE.toQuestionResponse(question);
    }

    @GetMapping(value = "/{partId:.+}/listTop10Question")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("List top 10 question successfully")
    public List<QuestionResponse> getTop10Question(
            @PathVariable("partId") UUID partId,
            @RequestParam("index") int index
    ) {

        List<QuestionEntity> questionList = questionService.getTop10Question(index, partId);

        return QuestionMapper.INSTANCE.toQuestionResponseList(questionList);
    }

    @GetMapping(value = "/{questionId:.+}/checkQuestionGroup")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Question has question group")
    public void checkQuestionGroup(@PathVariable("questionId") UUID questionId) {

        questionService.checkQuestionGroup(questionId);
    }

    @GetMapping(value = "/{questionId:.+}/listQuestionGroup")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("List question group successfully")
    public List<QuestionResponse> getQuestionGroupToQuestion(@PathVariable("questionId") UUID questionId) {

        List<QuestionEntity> questionEntityList = questionService.getQuestionGroupListByQuestionId(questionId);

        return QuestionMapper.INSTANCE.toQuestionResponseList(questionEntityList);
    }

    @GetMapping(value = "/{questionId:.+}/listAnswer")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("List answer to question successfully")
    public List<AnswerResponse> getAnswerToQuestion(@PathVariable("questionId") UUID questionId) {

        List<AnswerEntity> answerList = answerService.getListAnswerByQuestionId(questionId);

        return AnswerMapper.INSTANCE.toAnswerResponseList(answerList);
    }

    @DeleteMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Delete questions successfully")
    public void deleteQuestion(@RequestBody List<UUID> questionIds) {

        questionService.deleteAllQuestions(questionIds);
    }

    @GetMapping(value = "/{questionId:.+}/content")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Show content question successfully")
    public QuestionResponse getContentToQuestion(@PathVariable("questionId") UUID questionId) {

        QuestionEntity question = questionService.getQuestionById(questionId);

        return QuestionMapper.INSTANCE.toQuestionResponse(question);
    }

    @GetMapping("/list-question")
    @DefaultMessage("All question from part successfully")
    public List<QuestionPartResponse> getAllQuestionFromPart(
            @RequestParam("partName") String partName,
            @RequestParam("topicId") UUID topicId
    ) {

        return questionService.getAllPartQuestions(partName, topicId);
    }

}
