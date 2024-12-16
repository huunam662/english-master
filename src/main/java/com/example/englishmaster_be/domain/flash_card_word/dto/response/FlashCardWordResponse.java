package com.example.englishmaster_be.domain.flash_card_word.dto.response;

import com.example.englishmaster_be.domain.user.dto.response.UserBasicResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FlashCardWordResponse {

    UUID wordId;

    UUID flashCardId;

    String word;

    String image;

    String type;

    String spelling;

    String example;

    String note;

    String define;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss")
    LocalDateTime createAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss")
    LocalDateTime updateAt;

    UserBasicResponse userCreate;

    UserBasicResponse userUpdate;

}
