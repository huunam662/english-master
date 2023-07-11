package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Model.Answer;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class AnswerResponse {
    private UUID questionId;
    private UUID answerId;
    private String answerContent;
    private boolean correctAnswer;
    private String explainDetails;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    private JSONObject userCreate;

    private JSONObject userUpdate;

    public AnswerResponse(Answer answer) {
        questionId = answer.getQuestion().getQuestionId();
        answerId = answer.getAnswerId();
        answerContent = answer.getAnswerContent();
        correctAnswer = answer.isCorrectAnswer();
        explainDetails =answer.getExplainDetails();
        createAt = answer.getCreateAt();
        updateAt = answer.getUpdateAt();

        userCreate = new JSONObject();
        userUpdate = new JSONObject();

        userCreate.put("User Id", answer.getUserCreate().getUserId());
        userCreate.put("User Name", answer.getUserCreate().getName());

        userUpdate.put("User Id", answer.getUserUpdate().getUserId());
        userUpdate.put("User Name", answer.getUserUpdate().getName());
    }
}
