package com.example.englishmaster_be.domain.exam.question.controller;


import com.example.englishmaster_be.domain.exam.answer.service.IAnswerService;
import com.example.englishmaster_be.domain.exam.question.dto.res.QuestionPartRes;
import com.example.englishmaster_be.domain.exam.question.service.IQuestionService;
import com.example.englishmaster_be.domain.exam.answer.mapper.AnswerMapper;
import com.example.englishmaster_be.domain.exam.answer.dto.res.AnswerRes;
import com.example.englishmaster_be.domain.exam.answer.model.AnswerEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Tag(name = "Question")
@RestController
@RequestMapping("/question")
public class QuestionController {

    private final IQuestionService questionService;
    private final IAnswerService answerService;

    public QuestionController(IQuestionService questionService, IAnswerService answerService) {
        this.questionService = questionService;
        this.answerService = answerService;
    }

    @GetMapping(value = "/{questionId:.+}/listAnswer")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<AnswerRes> getAnswerToQuestion(@PathVariable("questionId") UUID questionId) {

        List<AnswerEntity> answerList = answerService.getListAnswerByQuestionId(questionId);

        return AnswerMapper.INSTANCE.toAnswerResponseList(answerList);
    }

    @DeleteMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteQuestion(@RequestBody List<UUID> questionIds) {

        questionService.deleteAllQuestions(questionIds);
    }

    @GetMapping("/list-question")
    public List<QuestionPartRes> getAllQuestionFromPart(
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
