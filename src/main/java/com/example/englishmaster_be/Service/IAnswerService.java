package com.example.englishmaster_be.Service;


import com.example.englishmaster_be.Model.*;

import java.util.UUID;

public interface IAnswerService {
    void createAnswer(Answer answer);
    boolean existQuestion(Answer answer, Question question);
    Answer findAnswerToId(UUID answerID);

    void deleteAnswer(Answer answer);

}
