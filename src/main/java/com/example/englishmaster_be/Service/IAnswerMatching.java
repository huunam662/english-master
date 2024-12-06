package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.DTO.Answer.AnswerMatchingRequest;
import com.example.englishmaster_be.Model.AnswerMatching;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IAnswerMatching {
    AnswerMatching createAnswerMatching(AnswerMatchingRequest request);

    Map<String,String> getListAnswerMatching(UUID questionId);
}
