package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Model.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.json.simple.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TopicResponse {

    UUID topicId;

    UUID packId;

    UUID statusId;

    String packName;

    String topicName;

    String topicImage;

    String topicDescription;

    String topicType;

    String workTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss")
    LocalDateTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss")
    LocalDateTime endTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss")
    LocalDateTime createAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss")
    LocalDateTime updateAt;

    UserBasicResponse userCreate;

    UserBasicResponse userUpdate;

    List<UUID> listPart;

    int numberQuestion;

    boolean isEnable;


    public TopicResponse(Topic topic) {

        if(Objects.isNull(topic)) return;

        if(Objects.nonNull(topic.getPack())) {
            this.packId = topic.getPack().getPackId();
            this.packName = topic.getPack().getPackName();
        }

        this.topicId = topic.getTopicId();
        this.topicName = topic.getTopicName();
        this.topicImage = topic.getTopicImage();
        this.topicDescription = topic.getTopicDescription();
        this.topicType = topic.getTopicType();
        this.numberQuestion = topic.getNumberQuestion();
        this.workTime = topic.getWorkTime();
        this.isEnable = topic.isEnable();

        this.listPart = Objects.nonNull(topic.getParts())
                ? topic.getParts().stream().map(Part::getPartId).toList()
                : new ArrayList<>();

        if(Objects.nonNull(topic.getStartTime()))
            this.startTime = topic.getStartTime();
        if(Objects.nonNull(topic.getEndTime()))
            this.endTime = topic.getEndTime();
        if(Objects.nonNull(topic.getCreateAt()))
            this.createAt = topic.getCreateAt();
        if(Objects.nonNull(topic.getUpdateAt()))
            this.updateAt = topic.getUpdateAt();
        if(Objects.nonNull(topic.getStatus()))
            this.statusId = topic.getStatus().getStatusId();

        if(Objects.nonNull(topic.getUserCreate()))
            userCreate = UserBasicResponse.builder()
                    .userId(topic.getUserCreate().getUserId())
                    .name(topic.getUserCreate().getName())
                    .build();

        if (Objects.nonNull(topic.getUserUpdate()))
            userUpdate = UserBasicResponse.builder()
                    .userId(topic.getUserUpdate().getUserId())
                    .name(topic.getUserUpdate().getName())
                    .build();
    }

}
