package com.example.englishmaster_be.model.response;

import com.example.englishmaster_be.model.Feedback;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.UUID;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeedbackResponse {

    UUID feedbackId;

    String name;

    String description;

    String avatar;

    String content;

    String createAt;

    String updateAt;

    boolean isEnable;

    public FeedbackResponse(Feedback feedback) {

        if(Objects.isNull(feedback)) return;

        this.feedbackId = feedback.getFeedbackId();
        this.name = feedback.getName();
        this.content = feedback.getContent();
        this.description = feedback.getDescription();
        this.avatar = feedback.getAvatar();
        this.isEnable = feedback.isEnable();

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");

        if(Objects.nonNull(feedback.getCreateAt()))
            this.createAt = sdf.format(Timestamp.valueOf(feedback.getCreateAt()));
        if(Objects.nonNull(feedback.getUpdateAt()))
            this.updateAt = sdf.format(Timestamp.valueOf(feedback.getUpdateAt()));

    }


}
