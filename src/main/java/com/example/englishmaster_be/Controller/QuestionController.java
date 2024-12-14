package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Configuration.global.annotation.MessageResponse;
import com.example.englishmaster_be.Mapper.AnswerMapper;
import com.example.englishmaster_be.Mapper.QuestionMapper;
import com.example.englishmaster_be.Model.Request.Question.GroupQuestionRequest;
import com.example.englishmaster_be.Model.Request.Question.QuestionRequest;
import com.example.englishmaster_be.Model.Request.UploadMultiFileRequest;
import com.example.englishmaster_be.Model.Response.AnswerResponse;
import com.example.englishmaster_be.Model.Response.QuestionBasicResponse;
import com.example.englishmaster_be.Model.Response.QuestionGroupResponse;
import com.example.englishmaster_be.Model.Response.QuestionResponse;
import com.example.englishmaster_be.Service.*;
import com.example.englishmaster_be.entity.AnswerEntity;
import com.example.englishmaster_be.entity.QuestionEntity;
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
    @MessageResponse("Create QuestionEntity successfully")
    public QuestionResponse createQuestion(
            @ModelAttribute QuestionRequest questionRequest
    ) {

        QuestionEntity question = questionService.saveQuestion(questionRequest);

        return QuestionMapper.INSTANCE.toQuestionResponse(question);
    }

    @PutMapping(value = "/{questionId:.+}/editQuestion")
    @PreAuthorize("hasRole('ADMIN')")
    @MessageResponse("Update QuestionEntity to TopicEntity successfully")
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
    @MessageResponse("Upload QuestionEntity successfully")
    public QuestionBasicResponse uploadFileQuestion(
            @PathVariable UUID questionId,
            @ModelAttribute UploadMultiFileRequest uploadMultiFileRequest
    ) {

        QuestionEntity question = questionService.uploadFileQuestion(questionId, uploadMultiFileRequest);

        return QuestionMapper.INSTANCE.toQuestionBasicResponse(question);
    }

    @PutMapping(value = "/{questionId:.+}/updatefile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @MessageResponse("Update file QuestionEntity successfully")
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
    @MessageResponse("Create QuestionEntity successfully")
    public QuestionResponse createGroupQuestion(
            @RequestBody GroupQuestionRequest groupQuestionRequest
    ) {

        QuestionEntity question = questionService.createGroupQuestion(groupQuestionRequest);

        return QuestionMapper.INSTANCE.toQuestionResponse(question);
    }


    @GetMapping(value = "/{partId:.+}/listTop10Question")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("List top 10 QuestionEntity successfully")
    public List<QuestionBasicResponse> getTop10Question(
            @PathVariable UUID partId,
            @RequestParam int indexp
    ) {

        List<QuestionEntity> questionList = questionService.getTop10Question(indexp, partId);

        return QuestionMapper.INSTANCE.toQuestionBasicResponseList(questionList);
    }

    @GetMapping(value = "/{questionId:.+}/checkQuestionGroup")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("QuestionEntity has QuestionEntity group")
    public void checkQuestionGroup(@PathVariable UUID questionId) {

        questionService.checkQuestionGroup(questionId);
    }

    @GetMapping(value = "/{questionId:.+}/listQuestionGroup")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("List QuestionEntity group successfully")
    public List<QuestionResponse> getQuestionGroupToQuestion(@PathVariable UUID questionId) {

        List<QuestionEntity> questionEntityList = questionService.getQuestionGroupListByQuestionId(questionId);

        return QuestionMapper.INSTANCE.toQuestionGroupChildrenResponseList(questionEntityList);
    }

    @GetMapping(value = "/{questionId:.+}/listAnswer")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("List AnswerEntity to QuestionEntity successfully")
    public List<AnswerResponse> getAnswerToQuestion(@PathVariable UUID questionId) {

        List<AnswerEntity> answerList = answerService.getListAnswerByQuestionId(questionId);

        return AnswerMapper.INSTANCE.toAnswerResponseList(answerList);
    }

    @DeleteMapping(value = "/{questionId:.+}/deleteQuestion")
    @PreAuthorize("hasRole('ADMIN')")
    @MessageResponse("Delete QuestionEntity successfully")
    public void deleteQuestion(@PathVariable UUID questionId) {

        questionService.deleteQuestion(questionId);
    }


    @GetMapping(value = "/{questionId:.+}/content")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Show ContentEntity QuestionEntity successfully")
    public QuestionResponse getContentToQuestion(@PathVariable UUID questionId) {

        QuestionEntity question = questionService.getQuestionById(questionId);

        return QuestionMapper.INSTANCE.toQuestionResponse(question);
    }

}
