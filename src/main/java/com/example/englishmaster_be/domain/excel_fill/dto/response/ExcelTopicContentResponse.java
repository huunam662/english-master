package com.example.englishmaster_be.domain.excel_fill.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExcelTopicContentResponse {

    String topicName;

    String topicImage;

    String topicDescription;

    String topicType;

    String packName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    Time workTime;

    Integer numberQuestion;

    List<String> partNamesList;

    List<String> partTypesList;

}
