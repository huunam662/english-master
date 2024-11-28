package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Model.*;

import java.util.List;
import java.util.UUID;


public interface IQuestionService {
    void createQuestion(Question question);

    Question findQuestionById(UUID questionId);

    void uploadFileQuestion(Question question);

    List<Question> getTop10Question(int index, UUID partId);

    int countQuestionToQuestionGroup(Question question);

    boolean checkQuestionGroup(Question question);

    List<Question> listQuestionGroup(Question question);

    void deleteQuestion(Question question);


}
