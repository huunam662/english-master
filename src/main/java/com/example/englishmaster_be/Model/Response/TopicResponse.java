package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Component.GetExtension;
import com.example.englishmaster_be.Component.PublicLink;
import com.example.englishmaster_be.Model.Topic;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class TopicResponse {
    private UUID topicId;

    private String topicName;

    private String topicImage;

    private String topicDescription;

    private String topicType;

    private String workTime;

    private String startTime;

    private String endTime;

    private String createAt;

    private String updateAt;

    private JSONObject userCreate;

    private JSONObject userUpdate;

    public TopicResponse(Topic topic) {

        String link = GetExtension.linkName(topic.getTopicImage());
        System.out.println(link);

        this.topicId = topic.getTopicId();
        this.topicName = topic.getTopicName();

        this.topicImage =  link + topic.getTopicImage();
        this.topicDescription = topic.getTopicDescription();
        this.topicType = topic.getTopicType();
        this.workTime = topic.getWorkTime();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

        this.startTime = sdf.format(Timestamp.valueOf(topic.getStartTime()));
        this.endTime = sdf.format(Timestamp.valueOf(topic.getEndTime()));
        this.createAt = sdf.format(Timestamp.valueOf(topic.getCreateAt()));
        this.updateAt = sdf.format(Timestamp.valueOf(topic.getUpdateAt()));

        userCreate = new JSONObject();
        userUpdate = new JSONObject();

        userCreate.put("User Id", topic.getUserCreate().getUserId());
        userCreate.put("User Name", topic.getUserCreate().getName());

        userUpdate.put("User Id", topic.getUserUpdate().getUserId());
        userUpdate.put("User Name", topic.getUserUpdate().getName());
    }

}
