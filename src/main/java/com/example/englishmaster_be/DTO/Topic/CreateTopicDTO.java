package com.example.englishmaster_be.DTO.Topic;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateTopicDTO {
    private String topicName;
    private MultipartFile topicImage;
    private String topicDiscription;
    private String topicType;
    private String workTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean enable;

    public CreateTopicDTO() {
    }
}
