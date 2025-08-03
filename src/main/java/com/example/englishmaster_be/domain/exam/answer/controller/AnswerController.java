package com.example.englishmaster_be.domain.exam.answer.controller;


<<<<<<< HEAD:src/main/java/com/example/englishmaster_be/domain/exam/answer/controller/AnswerController.java
import com.example.englishmaster_be.domain.exam.answer.dto.res.AnswerCorrectRes;
import com.example.englishmaster_be.domain.exam.answer.service.IAnswerService;
import com.example.englishmaster_be.domain.exam.answer.dto.req.AnswerReq;
import com.example.englishmaster_be.domain.exam.answer.mapper.AnswerMapper;
import com.example.englishmaster_be.domain.exam.answer.dto.res.AnswerRes;
import com.example.englishmaster_be.domain.exam.answer.model.AnswerEntity;
=======
import com.example.englishmaster_be.domain.answer.dto.response.AnswerCorrectResponse;
import com.example.englishmaster_be.domain.answer.service.IAnswerService;
import com.example.englishmaster_be.domain.answer.dto.request.AnswerRequest;
import com.example.englishmaster_be.domain.answer.mapper.AnswerMapper;
import com.example.englishmaster_be.domain.answer.dto.response.AnswerResponse;
import com.example.englishmaster_be.domain.answer.model.AnswerEntity;
>>>>>>> 197ed81940903ab14a285d25c6aed6f94b8e649c:src/main/java/com/example/englishmaster_be/domain/answer/controller/AnswerController.java
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
<<<<<<< HEAD:src/main/java/com/example/englishmaster_be/domain/exam/answer/controller/AnswerController.java
    public AnswerRes createAnswer(@RequestBody AnswerReq answerRequest) {
=======
    public AnswerResponse createAnswer(@RequestBody AnswerRequest answerRequest) {
>>>>>>> 197ed81940903ab14a285d25c6aed6f94b8e649c:src/main/java/com/example/englishmaster_be/domain/answer/controller/AnswerController.java

        AnswerEntity answer = answerService.saveAnswer(answerRequest);

        return AnswerMapper.INSTANCE.toAnswerResponse(answer);
    }

    @PutMapping(value = "/{answerId:.+}/update")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
<<<<<<< HEAD:src/main/java/com/example/englishmaster_be/domain/exam/answer/controller/AnswerController.java
    public AnswerRes updateAnswer(@PathVariable("answerId") UUID answerId, @RequestBody AnswerReq answerRequest) {
=======
    public AnswerResponse updateAnswer(@PathVariable("answerId") UUID answerId, @RequestBody AnswerRequest answerRequest) {
>>>>>>> 197ed81940903ab14a285d25c6aed6f94b8e649c:src/main/java/com/example/englishmaster_be/domain/answer/controller/AnswerController.java

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
<<<<<<< HEAD:src/main/java/com/example/englishmaster_be/domain/exam/answer/controller/AnswerController.java
    public AnswerRes getDetailAnswer(@PathVariable("answerId") UUID answerId) {
=======
    public AnswerResponse getDetailAnswer(@PathVariable("answerId") UUID answerId) {
>>>>>>> 197ed81940903ab14a285d25c6aed6f94b8e649c:src/main/java/com/example/englishmaster_be/domain/answer/controller/AnswerController.java

        AnswerEntity answer = answerService.getAnswerById(answerId);

        return AnswerMapper.INSTANCE.toAnswerResponse(answer);
    }

    @GetMapping(value = "/{answerId:.+}/checkCorrect")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
<<<<<<< HEAD:src/main/java/com/example/englishmaster_be/domain/exam/answer/controller/AnswerController.java
    public AnswerCorrectRes checkCorrectAnswer(@PathVariable("answerId") UUID answerId) {
=======
    public AnswerCorrectResponse checkCorrectAnswer(@PathVariable("answerId") UUID answerId) {
>>>>>>> 197ed81940903ab14a285d25c6aed6f94b8e649c:src/main/java/com/example/englishmaster_be/domain/answer/controller/AnswerController.java

        AnswerEntity answer = answerService.getAnswerById(answerId);

        return AnswerMapper.INSTANCE.toCheckCorrectAnswerResponse(answer);
    }

}
