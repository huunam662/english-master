package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Model.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.json.simple.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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

    String startTime;

    String endTime;

    String createAt;

    String updateAt;

    JSONObject userCreate;

    JSONObject userUpdate;

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

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");

        if(Objects.nonNull(topic.getStartTime()))
            this.startTime = sdf.format(Timestamp.valueOf(topic.getStartTime()));
        if(Objects.nonNull(topic.getEndTime()))
            this.endTime = sdf.format(Timestamp.valueOf(topic.getEndTime()));
        if(Objects.nonNull(topic.getCreateAt()))
            this.createAt = sdf.format(Timestamp.valueOf(topic.getCreateAt()));
        if(Objects.nonNull(topic.getUpdateAt()))
            this.updateAt = sdf.format(Timestamp.valueOf(topic.getUpdateAt()));

        userCreate = new JSONObject();
        userUpdate = new JSONObject();

        if(Objects.nonNull(topic.getUserCreate())){
            userCreate.put("User Id", topic.getUserCreate().getUserId());
            userCreate.put("User Name", topic.getUserCreate().getName());
        }
        if(Objects.nonNull(topic.getUserUpdate())){
            userUpdate.put("User Id", topic.getUserUpdate().getUserId());
            userUpdate.put("User Name", topic.getUserUpdate().getName());
        }

        if(Objects.nonNull(topic.getStatus()))
            this.statusId = topic.getStatus().getStatusId();
    }

}
