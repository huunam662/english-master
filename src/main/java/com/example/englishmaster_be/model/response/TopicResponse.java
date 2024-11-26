package com.example.englishmaster_be.model.response;

import com.example.englishmaster_be.model.*;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class TopicResponse {
    private UUID topicId;
    private UUID packId;
    private String packName;

    private String topicName;

    private String topicImage;

    private String topicDescription;

    private String topicType;

    private List<UUID> listPart;

    private int numberQuestion;

    private String workTime;

    private String startTime;

    private String endTime;

    private String createAt;

    private String updateAt;

    private boolean isEnable;

    private JSONObject userCreate;

    private JSONObject userUpdate;

    private UUID statusId;

    public TopicResponse(Topic topic) {


        this.topicId = topic.getTopicId();
        this.packId = topic.getPack().getPackId();
        this.packName = topic.getPack().getPackName();
        this.topicName = topic.getTopicName();

        this.topicImage = topic.getTopicImage();
        this.topicDescription = topic.getTopicDescription();
        this.topicType = topic.getTopicType();
        this.numberQuestion = topic.getNumberQuestion();
        this.workTime = topic.getWorkTime();
        this.isEnable = topic.isEnable();

        List<UUID> listPart = new ArrayList<>();
        for (Part part : topic.getParts()) {
            listPart.add(part.getPartId());
        }
        this.listPart = listPart;

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");

        if (topic.getStartTime() != null && topic.getEndTime() != null &&
                topic.getCreateAt() != null && topic.getUpdateAt() != null) {

            this.startTime = sdf.format(Timestamp.valueOf(topic.getStartTime()));
            this.endTime = sdf.format(Timestamp.valueOf(topic.getEndTime()));

        }
        this.createAt = sdf.format(Timestamp.valueOf(topic.getCreateAt()));
        this.updateAt = sdf.format(Timestamp.valueOf(topic.getUpdateAt()));

        userCreate = new JSONObject();
        userUpdate = new JSONObject();

        userCreate.put("User Id", topic.getUserCreate().getUserId());
        userCreate.put("User Name", topic.getUserCreate().getName());

        userUpdate.put("User Id", topic.getUserUpdate().getUserId());
        userUpdate.put("User Name", topic.getUserUpdate().getName());

        this.statusId = topic.getStatus().getStatusId();
    }

}
