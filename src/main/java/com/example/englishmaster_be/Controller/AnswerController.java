package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Configuration.global.annotation.MessageResponse;
import com.example.englishmaster_be.Model.Response.ExceptionResponseModel;
import com.example.englishmaster_be.Model.Response.ResponseModel;
import com.example.englishmaster_be.DTO.Answer.*;
import com.example.englishmaster_be.Model.Response.*;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Service.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/answer")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AnswerController {

    IAnswerService answerService;

    @PostMapping(value = "/create")
    @PreAuthorize("hasRole('ADMIN')")
    public AnswerResponse createAnswer(@RequestBody CreateAnswerDTO createAnswerDTO) {

        Answer answer = answerService.saveAnswer(createAnswerDTO);;

        return new AnswerResponse(answer);
    }

    @PutMapping(value = "/{answerId:.+}/update")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public AnswerResponse updateAnswer(@PathVariable UUID answerId, @RequestBody UpdateAnswerDTO updateAnswerDTO) {

        updateAnswerDTO.setAnswerId(answerId);

        Answer answer = answerService.saveAnswer(updateAnswerDTO);;

        return new AnswerResponse(answer);
    }

    @DeleteMapping(value = "/{answerId:.+}/delete")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Delete Answer successfully")
    public void deleteAnswer(@PathVariable UUID answerId) {

        answerService.deleteAnswer(answerId);
    }

    @GetMapping(value = "/{answerId:.+}/getDetailAnswer")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Detail Answer successfully")
    public AnswerResponse getDetailAnswer(@PathVariable UUID answerId) {

        Answer answer = answerService.findAnswerToId(answerId);

        return new AnswerResponse(answer);
    }

    @GetMapping(value = "/{answerId:.+}/checkCorrect")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Check Answer successfully")
    public CheckCorrectAnswerResponse checkCorrectAnswer(@PathVariable UUID answerId) {

        Answer answer = answerService.findAnswerToId(answerId);

        return CheckCorrectAnswerResponse.builder()
                .correctAnswer(answer.isCorrectAnswer())
                .scoreAnswer(answer.getQuestion().getQuestionScore())
                .build();
    }

}
