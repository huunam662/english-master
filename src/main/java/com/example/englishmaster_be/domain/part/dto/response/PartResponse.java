package com.example.englishmaster_be.domain.part.dto.response;

import com.example.englishmaster_be.domain.user.dto.response.UserBasicRes;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PartResponse {

    UUID partId;

    String partName;

    String partDescription;

    String partType;

    String contentType;

    String contentData;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss")
    LocalDateTime createAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss")
    LocalDateTime updateAt;

    UserBasicRes userCreate;

    UserBasicRes userUpdate;

}
