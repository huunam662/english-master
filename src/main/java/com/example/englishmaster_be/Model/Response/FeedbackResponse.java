package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Helper.GetExtension;
import com.example.englishmaster_be.Model.Feedback;
import com.example.englishmaster_be.Model.News;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.UUID;

@Getter
@Setter
public class FeedbackResponse {
    private UUID feedbackId;
    private String name;
    private String description;
    private String avatar;
    private String content;
    private boolean isEnable;
    private String createAt;
    private String updateAt;

    public FeedbackResponse(Feedback feedback) {
        this.feedbackId = feedback.getFeedbackId();
        this.name = feedback.getName();
        this.content = feedback.getContent();
        this.description = feedback.getDescription();

        this.avatar = feedback.getAvatar();
        this.isEnable = feedback.isEnable();

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");

        this.createAt = sdf.format(Timestamp.valueOf(feedback.getCreateAt()));
        this.updateAt = sdf.format(Timestamp.valueOf(feedback.getUpdateAt()));

    }


}
