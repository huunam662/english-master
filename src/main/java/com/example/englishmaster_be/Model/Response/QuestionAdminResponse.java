package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.entity.AnswerEntity;
import com.example.englishmaster_be.entity.QuestionEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionAdminResponse {

    UUID questionId;

    UUID answerCorrect;

    UUID partId;

    String questionContent;

    String createAt;

    String updateAt;

    List<AnswerResponse> listAnswer;

    List<QuestionResponse> questionGroup;

    Integer questionScore;

    List<ContentResponse> contentList;

}
