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

    @GetMapping("/list-question")
    @DefaultMessage("All question from part successfully")
    public List<QuestionPartResponse> getAllQuestionFromPart(
            @RequestParam("partName") String partName,
            @RequestParam("topicId") UUID topicId
    ) {

        return questionService.getAllPartQuestions(partName, topicId);
    }

}
