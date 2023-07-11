package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Component.GetExtension;
import com.example.englishmaster_be.Model.Question;
import com.example.englishmaster_be.Model.Content;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class QuestionResponse {
    private UUID questionId;
    private String questionContent;
    private int questionScore;
    private JSONArray contentList;
    private UUID questionGroup;
    private UUID partId;
    private String createAt;
    private String updateAt;

    private JSONObject userCreate;

    private JSONObject userUpdate;

    public QuestionResponse(Question question){
        String link;

        this.questionId = question.getQuestionId();
        this.questionContent = question.getQuestionContent();
        this.questionScore = question.getQuestionScore();

        if(question.getQuestionGroup() == null){
            this.questionGroup = null;
        }
        else{
            this.questionGroup = question.getQuestionGroup().getQuestionId();
        }

        this.partId = question.getPart().getPartId();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        this.createAt = sdf.format(Timestamp.valueOf(question.getCreateAt()));
        this.updateAt = sdf.format(Timestamp.valueOf(question.getUpdateAt()));

        contentList = new JSONArray();
        userCreate = new JSONObject();
        userUpdate = new JSONObject();

        if(question.getContentCollection() != null){
            for(Content content1 : question.getContentCollection()){
                JSONObject content = new JSONObject();
                content.put("Content Type", content1.getContentType());

                if(content1.getContentData() == null){
                    content.put("Content Data", content1.getContentData());

                }else {
                    link = GetExtension.linkName(content1.getContentData());
                    content.put("Content Data", link + content1.getContentData());
                }

                contentList.add(content);
            }
        }


        userCreate.put("User Id", question.getUserCreate().getUserId());
        userCreate.put("User Name", question.getUserCreate().getName());

        userUpdate.put("User Id", question.getUserUpdate().getUserId());
        userUpdate.put("User Name", question.getUserUpdate().getName());
    }
}
