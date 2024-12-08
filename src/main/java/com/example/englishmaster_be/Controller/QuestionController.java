package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Configuration.global.annotation.MessageResponse;
import com.example.englishmaster_be.Mapper.QuestionMapper;
import com.example.englishmaster_be.Model.Response.ExceptionResponseModel;
import com.example.englishmaster_be.Model.Response.ResponseModel;
import com.example.englishmaster_be.DTO.Answer.SaveListAnswerDTO;
import com.example.englishmaster_be.Helper.GetExtension;
import com.example.englishmaster_be.DTO.Question.*;
import com.example.englishmaster_be.DTO.*;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.*;
import com.example.englishmaster_be.Repository.AnswerRepository;
import com.example.englishmaster_be.Service.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@Tag(name = "Question")
@RestController
@RequestMapping("/api/question")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionController {

    IQuestionService questionService;

    IAnswerService answerService;


    @PostMapping(value = "/create")
    @PreAuthorize("hasRole('ADMIN')")
    @MessageResponse("Create Question successfully")
    public QuestionResponse createQuestion(
            @ModelAttribute SaveQuestionDTO saveQuestionDTO
    ) {

        return questionService.saveQuestion(saveQuestionDTO);
    }

    @PutMapping(value = "/{questionId:.+}/uploadfile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Upload Question successfully")
    public QuestionResponse uploadFileQuestion(
            @PathVariable UUID questionId,
            @ModelAttribute UploadMultiFileDTO uploadMultiFileDTO
    ) {

        return questionService.uploadFileQuestion(questionId, uploadMultiFileDTO);
    }

    @PutMapping(value = "/{questionId:.+}/updatefile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @MessageResponse("Update file Question successfully")
    public QuestionResponse updateFileQuestion(
            @PathVariable UUID questionId,
            @RequestParam("oldFileName") String oldFileName,
            @RequestPart MultipartFile newFile
    ) {

        return questionService.updateFileQuestion(questionId, oldFileName, newFile);
    }

    @PostMapping(value = "/create/groupQuestion")
    @PreAuthorize("hasRole('ADMIN')")
    @MessageResponse("Create Question successfully")
    public QuestionResponse createGroupQuestion(
            @RequestBody SaveGroupQuestionDTO createGroupQuestionDTO
    ) {

        return questionService.createGroupQuestion(createGroupQuestionDTO);
    }


    @GetMapping(value = "/{partId:.+}/listTop10Question")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("List top 10 Question successfully")
    public List<QuestionResponse> getTop10Question(
            @PathVariable UUID partId,
            @RequestParam int indexp
    ) {

        List<Question> questionList = questionService.getTop10Question(indexp, partId);

        return QuestionMapper.INSTANCE.toQuestionResponseList(questionList);
    }

    @GetMapping(value = "/{questionId:.+}/checkQuestionGroup")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Question has Question group")
    public void checkQuestionGroup(@PathVariable UUID questionId) {

        questionService.checkQuestionGroup(questionId);
    }

    @GetMapping(value = "/{questionId:.+}/listQuestionGroup")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("List Question group successfully")
    public List<QuestionGroupResponse> getQuestionGroupToQuestion(@PathVariable UUID questionId) {

        return questionService.getQuestionGroupToQuestion(questionId);
    }

    @GetMapping(value = "/{questionId:.+}/listAnswer")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("List Answer to Question successfully")
    public List<AnswerResponse> getAnswerToQuestion(@PathVariable UUID questionId) {

        return answerService.getListAnswerByQuestionId(questionId);
    }

    @DeleteMapping(value = "/{questionId:.+}/deleteQuestion")
    @PreAuthorize("hasRole('ADMIN')")
    @MessageResponse("Delete Question successfully")
    public void deleteQuestion(@PathVariable UUID questionId) {

        questionService.deleteQuestion(questionId);
    }

    @PutMapping(value = "/{questionId:.+}/editQuestion")
    @PreAuthorize("hasRole('ADMIN')")
    @MessageResponse("Update Question to Topic successfully")
    public QuestionResponse editQuestion(
            @PathVariable UUID questionId,
            @ModelAttribute UpdateQuestionDTO updateQuestionDTO
    ) {

        updateQuestionDTO.setUpdateQuestionId(questionId);

        return questionService.saveQuestion(updateQuestionDTO);
    }

    @GetMapping(value = "/{questionId:.+}/content")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Show Content Question successfully")
    public QuestionResponse getContentToQuestion(@PathVariable UUID questionId) {

        Question question = questionService.getQuestionById(questionId);

        return QuestionMapper.INSTANCE.toQuestionResponse(question);
    }

}
