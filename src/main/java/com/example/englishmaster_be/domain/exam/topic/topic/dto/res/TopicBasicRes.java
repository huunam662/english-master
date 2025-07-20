package com.example.englishmaster_be.domain.exam.topic.topic.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class TopicBasicRes {

    private UUID topicId;
    private String topicName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime workTime;
    private String topicType;

}
