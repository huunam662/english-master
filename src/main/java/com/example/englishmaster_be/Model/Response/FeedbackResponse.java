package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Model.Feedback;
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

    UUID FeedbackId;

    String name;

    String description;

    String avatar;

    String content;

    String createAt;

    String updateAt;

    boolean isEnable;

    public FeedbackResponse(Feedback Feedback) {

        if(Objects.isNull(Feedback)) return;

        this.FeedbackId = Feedback.getId();
        this.name = Feedback.getName();
        this.content = Feedback.getContent();
        this.description = Feedback.getDescription();
        this.avatar = Feedback.getAvatar();
        this.isEnable = Feedback.isEnable();

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");

        if(Objects.nonNull(Feedback.getCreateAt()))
            this.createAt = sdf.format(Timestamp.valueOf(Feedback.getCreateAt()));
        if(Objects.nonNull(Feedback.getUpdateAt()))
            this.updateAt = sdf.format(Timestamp.valueOf(Feedback.getUpdateAt()));

    }


}
