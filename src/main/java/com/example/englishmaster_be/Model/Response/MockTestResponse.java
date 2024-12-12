package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.entity.MockTestEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MockTestResponse {

    UUID mockTestId;

    UUID topicId;

    Integer correctAnswers;

    Integer score;

    Time time;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss")
    LocalDateTime createAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss")
    LocalDateTime updateAt;

    UserBasicResponse user;

    UserBasicResponse userCreate;

    UserBasicResponse userUpdate;

}
