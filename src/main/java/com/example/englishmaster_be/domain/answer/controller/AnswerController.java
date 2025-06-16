package com.example.englishmaster_be.domain.answer.controller;


import com.example.englishmaster_be.domain.answer.dto.response.AnswerCorrectResponse;
import com.example.englishmaster_be.domain.answer.service.IAnswerService;
import com.example.englishmaster_be.domain.answer.dto.request.AnswerRequest;
import com.example.englishmaster_be.domain.answer.mapper.AnswerMapper;
import com.example.englishmaster_be.domain.answer.dto.response.AnswerResponse;
import com.example.englishmaster_be.domain.answer.model.AnswerEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Answer")
@RestController
@RequestMapping("/answer")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AnswerController {

    IAnswerService answerService;


    @PostMapping(value = "/create")
    @PreAuthorize("hasRole('ADMIN')")
    public AnswerResponse createAnswer(@RequestBody AnswerRequest answerRequest) {

        AnswerEntity answer = answerService.saveAnswer(answerRequest);

        return AnswerMapper.INSTANCE.toAnswerResponse(answer);
    }

    @PutMapping(value = "/{answerId:.+}/update")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public AnswerResponse updateAnswer(@PathVariable("answerId") UUID answerId, @RequestBody AnswerRequest answerRequest) {

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
    public AnswerResponse getDetailAnswer(@PathVariable("answerId") UUID answerId) {

        AnswerEntity answer = answerService.getAnswerById(answerId);

        return AnswerMapper.INSTANCE.toAnswerResponse(answer);
    }

    @GetMapping(value = "/{answerId:.+}/checkCorrect")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public AnswerCorrectResponse checkCorrectAnswer(@PathVariable("answerId") UUID answerId) {

        AnswerEntity answer = answerService.getAnswerById(answerId);

        return AnswerMapper.INSTANCE.toCheckCorrectAnswerResponse(answer);
    }

}
