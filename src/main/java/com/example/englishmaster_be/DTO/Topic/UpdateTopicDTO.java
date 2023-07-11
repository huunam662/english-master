package com.example.englishmaster_be.DTO.Topic;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class UpdateTopicDTO {
    private UUID topicId;
    private String topicName;
    private String topicDiscription;
    private String topicType;
    private String workTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean enable;

    public UpdateTopicDTO() {
    }
}
