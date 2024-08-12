package com.example.englishmaster_be.DTO.Topic;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateTopicByExcelFileDTO {
    String topicName;
    String topicImageName;
    String topicDescription;
    String topicType;
    String workTime;
    int numberQuestion;
    UUID topicPackId;

    List<UUID> listPart;

    @Parameter(
            example = "2023-09-06T14:30:00",
            schema = @Schema(type = "string", format = "date-time", pattern = "yyyy-MM-dd'T'HH:mm:ss")
    )
    LocalDateTime startTime;

    @Parameter(
            example = "2023-09-06T14:30:00",
            schema = @Schema(type = "string", format = "date-time", pattern = "yyyy-MM-dd'T'HH:mm:ss")
    )
    LocalDateTime endTime;
}
