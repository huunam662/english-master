package com.example.englishmaster_be.domain.excel_fill.dto.response;

import com.example.englishmaster_be.domain.pack.dto.response.PackResponse;
import com.example.englishmaster_be.domain.part.dto.response.PartResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExcelTopicResponse {

    UUID topicId;

    String topicName;

    String topicImage;

    String topicDescription;

    String topicType;

    PackResponse pack;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    LocalTime workTime;

    Integer numberQuestion;

    List<PartResponse> parts;

}
