package com.example.englishmaster_be.domain.excel.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;
import java.util.List;

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
    LocalTime workTime;

    Integer numberQuestion;

    List<String> partNamesList;

    List<String> partTypesList;

}
