package com.example.englishmaster_be.model.response;

import com.example.englishmaster_be.model.Content;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.Objects;
import java.util.UUID;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContentResponse {

    UUID contentId;

    UUID topicId;

    UUID questionId;

    String contentType;

    String contentData;

    String code;


    public ContentResponse(Content content) {

        if(Objects.isNull(content)) return;

        this.contentId = content.getContentId();
        this.contentType = content.getContentType();
        this.contentData = content.getContentData();
        this.topicId = content.getTopicId();
        this.code = content.getCode();

        if(Objects.nonNull(content.getQuestion()))
            this.questionId = content.getQuestion().getQuestionId();
    }

}
