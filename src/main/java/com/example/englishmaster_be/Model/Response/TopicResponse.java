package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Component.PublicLink;
import com.example.englishmaster_be.Model.Topic;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

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

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;

    private JSONObject userCreate;

    private JSONObject userUpdate;

    public TopicResponse(Topic topic) {

        String link = PublicLink.getLink();
        String extension = getExtension(topic.getTopicImage());
        String linkFile = link;

        this.topicId = topic.getTopicId();
        this.topicName = topic.getTopicName();

        switch (extension){
            case ".jpg":
            case ".JPG":
            case ".jpeg":
            case ".JPEG":
            case ".png":
            case ".PNG":
            case ".gif":
            case ".GIF":
                linkFile = link + "file/showImage/";
                break;
            default:
                linkFile = link + "file/";
                break;
        }

        this.topicImage =  linkFile + topic.getTopicImage();
        this.topicDescription = topic.getTopicDescription();
        this.topicType = topic.getTopicType();
        this.workTime = topic.getWorkTime();
        this.startTime = topic.getStartTime();
        this.endTime = topic.getEndTime();
        this.createAt = topic.getCreateAt();
        this.updateAt = topic.getUpdateAt();

        userCreate = new JSONObject();
        userUpdate = new JSONObject();

        userCreate.put("User Id", topic.getUserCreate().getUserId());
        userCreate.put("User Name", topic.getUserCreate().getName());

        userUpdate.put("User Id", topic.getUserUpdate().getUserId());
        userUpdate.put("User Name", topic.getUserUpdate().getName());
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex > -1 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex);
        }
        return "";
    }
}
