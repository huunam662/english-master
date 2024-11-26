package com.example.englishmaster_be.service;


import com.example.englishmaster_be.model.*;

import java.util.UUID;

public interface IAnswerService {
    void createAnswer(Answer answer);
    boolean existQuestion(Answer answer, Question question);
    Answer findAnswerToId(UUID answerID);

    boolean checkCorrectAnswer(UUID answerId);

    int scoreAnswer(UUID answerId);

    void deleteAnswer(Answer answer);
    Answer correctAnswer(Question question);
    Answer choiceAnswer(Question question, MockTest mockTest);
}
