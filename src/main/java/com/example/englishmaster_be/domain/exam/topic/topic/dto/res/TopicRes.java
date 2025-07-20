package com.example.englishmaster_be.domain.exam.topic.topic.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class TopicRes {

    private UUID topicId;
    private String packName;
    private UUID packId;
    private String topicName;
    private String topicImage;
    private String topicAudio;
    private String topicDescription;
    private String topicType;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime workTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private List<String> partNames;
    private Integer numberQuestion;
    private Boolean enable;

}
