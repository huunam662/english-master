package com.example.englishmaster_be.domain.question.controller;


import com.example.englishmaster_be.domain.answer.service.IAnswerService;
import com.example.englishmaster_be.domain.question.dto.response.QuestionPartResponse;
import com.example.englishmaster_be.domain.question.service.IQuestionService;
import com.example.englishmaster_be.domain.answer.mapper.AnswerMapper;
import com.example.englishmaster_be.domain.answer.dto.response.AnswerResponse;
import com.example.englishmaster_be.domain.answer.model.AnswerEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public List<AnswerResponse> getAnswerToQuestion(@PathVariable("questionId") UUID questionId) {

        List<AnswerEntity> answerList = answerService.getListAnswerByQuestionId(questionId);

        return AnswerMapper.INSTANCE.toAnswerResponseList(answerList);
    }

    @DeleteMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteQuestion(@RequestBody List<UUID> questionIds) {

        questionService.deleteAllQuestions(questionIds);
    }

    @GetMapping("/list-question")
    public List<QuestionPartResponse> getAllQuestionFromPart(
            @RequestParam("partName") String partName,
            @RequestParam("topicId") UUID topicId
    ) {

        return questionService.getAllPartQuestions(partName, topicId);
    }

    @Operation(
            summary = "Update order question number of topic to topic id.",
            description = "Update order question number of topic to topic id."
    )
    @PatchMapping("/order-question-number")
    public void orderQuestionNumberToTopicId(@RequestParam("topicId") UUID topicId){
        questionService.orderQuestionNumberToTopicId(topicId);
    }

}
