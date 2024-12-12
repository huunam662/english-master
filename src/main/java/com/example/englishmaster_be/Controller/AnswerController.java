package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Configuration.global.annotation.MessageResponse;
import com.example.englishmaster_be.Model.Request.Answer.AnswerRequest;
import com.example.englishmaster_be.Mapper.AnswerMapper;
import com.example.englishmaster_be.Model.Response.AnswerResponse;
import com.example.englishmaster_be.Model.Response.CheckCorrectAnswerResponse;
import com.example.englishmaster_be.Service.*;
import com.example.englishmaster_be.entity.AnswerEntity;
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
    @MessageResponse("Save answer successfully")
    public AnswerResponse createAnswer(@RequestBody AnswerRequest answerRequest) {

        AnswerEntity answer = answerService.saveAnswer(answerRequest);

        return AnswerMapper.INSTANCE.toAnswerResponse(answer);
    }

    @PutMapping(value = "/{answerId:.+}/update")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Save answer successfully")
    public AnswerResponse updateAnswer(@PathVariable UUID answerId, @RequestBody AnswerRequest answerRequest) {

        answerRequest.setAnswerId(answerId);

        AnswerEntity answer = answerService.saveAnswer(answerRequest);

        return AnswerMapper.INSTANCE.toAnswerResponse(answer);
    }

    @DeleteMapping(value = "/{answerId:.+}/delete")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Delete AnswerEntity successfully")
    public void deleteAnswer(@PathVariable UUID answerId) {

        answerService.deleteAnswer(answerId);
    }

    @GetMapping(value = "/{answerId:.+}/getDetailAnswer")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Detail AnswerEntity successfully")
    public AnswerResponse getDetailAnswer(@PathVariable UUID answerId) {

        AnswerEntity answer = answerService.getAnswerById(answerId);

        return AnswerMapper.INSTANCE.toAnswerResponse(answer);
    }

    @GetMapping(value = "/{answerId:.+}/checkCorrect")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Check AnswerEntity successfully")
    public CheckCorrectAnswerResponse checkCorrectAnswer(@PathVariable UUID answerId) {

        AnswerEntity answer = answerService.getAnswerById(answerId);

        return AnswerMapper.INSTANCE.toCheckCorrectAnswerResponse(answer);
    }

}
