package com.example.englishmaster_be.Service;


import com.example.englishmaster_be.DTO.Answer.SaveAnswerDTO;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.AnswerResponse;

import java.util.List;
import java.util.UUID;

public interface IAnswerService {

    Answer saveAnswer(SaveAnswerDTO answer);

    boolean existQuestion(Answer answer, Question question);

    Answer findAnswerToId(UUID answerID);

    void deleteAnswer(UUID answerId);

    Answer correctAnswer(Question question);

    Answer choiceAnswer(Question question, MockTest mockTest);

    List<AnswerResponse> getListAnswerByQuestionId(UUID questionId);
}
