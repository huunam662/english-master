package com.example.englishmaster_be.domain.exam.answer.controller;


import com.example.englishmaster_be.domain.exam.answer.dto.res.AnswerCorrectRes;
import com.example.englishmaster_be.domain.exam.answer.service.IAnswerService;
import com.example.englishmaster_be.domain.exam.answer.dto.req.AnswerReq;
import com.example.englishmaster_be.domain.exam.answer.mapper.AnswerMapper;
import com.example.englishmaster_be.domain.exam.answer.dto.res.AnswerRes;
import com.example.englishmaster_be.domain.exam.answer.model.AnswerEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Answer")
@RestController
@RequestMapping("/answer")
public class AnswerController {

    private final IAnswerService answerService;

    public AnswerController(IAnswerService answerService) {
        this.answerService = answerService;
    }

    @PostMapping(value = "/create")
    @PreAuthorize("hasRole('ADMIN')")
    public AnswerRes createAnswer(@RequestBody AnswerReq answerRequest) {

        AnswerEntity answer = answerService.saveAnswer(answerRequest);

        return AnswerMapper.INSTANCE.toAnswerResponse(answer);
    }

    @PutMapping(value = "/{answerId:.+}/update")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public AnswerRes updateAnswer(@PathVariable("answerId") UUID answerId, @RequestBody AnswerReq answerRequest) {

        answerRequest.setAnswerId(answerId);

        AnswerEntity answer = answerService.saveAnswer(answerRequest);

        return AnswerMapper.INSTANCE.toAnswerResponse(answer);
    }

    @DeleteMapping(value = "/{answerId:.+}/delete")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public void deleteAnswer(@PathVariable("answerId") UUID answerId) {

        answerService.deleteAnswer(answerId);
    }

    @GetMapping(value = "/{answerId:.+}/getDetailAnswer")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public AnswerRes getDetailAnswer(@PathVariable("answerId") UUID answerId) {

        AnswerEntity answer = answerService.getAnswerById(answerId);

        return AnswerMapper.INSTANCE.toAnswerResponse(answer);
    }

    @GetMapping(value = "/{answerId:.+}/checkCorrect")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public AnswerCorrectRes checkCorrectAnswer(@PathVariable("answerId") UUID answerId) {

        AnswerEntity answer = answerService.getAnswerById(answerId);

        return AnswerMapper.INSTANCE.toCheckCorrectAnswerResponse(answer);
    }

}
