package com.example.englishmaster_be.domain.exam.topic.topic.dto.res;

import com.example.englishmaster_be.domain.exam.topic.type.dto.res.TopicTypeRes;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class TopicAndTypeRes {

    private UUID topicId;
    private String topicName;
    private String topicImage;
    private String topicAudio;
    private String topicDescription;
    private LocalTime workTime;
    private Integer numberQuestion;
    private Boolean enable;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private TopicTypeRes topicType;

}
