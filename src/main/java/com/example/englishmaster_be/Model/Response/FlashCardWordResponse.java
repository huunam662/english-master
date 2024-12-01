package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Model.FlashCardWord;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Objects;
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

    String createAt;

    String updateAt;

    UserBasicResponse userCreate;

    UserBasicResponse userUpdate;

    public FlashCardWordResponse(FlashCardWord flashCardWord) {

        if(Objects.isNull(flashCardWord)) return;

        this.image = flashCardWord.getImage();
        this.wordId = flashCardWord.getWordId();
        this.flashCardId = flashCardWord.getFlashCard().getFlashCardId();
        this.word = flashCardWord.getWord();
        this.type = flashCardWord.getType();
        this.spelling = flashCardWord.getSpelling();
        this.example = flashCardWord.getExample();
        this.note = flashCardWord.getNote();
        this.define = flashCardWord.getDefine();

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
        this.createAt = sdf.format(Timestamp.valueOf(flashCardWord.getCreateAt()));
        this.updateAt = sdf.format(Timestamp.valueOf(flashCardWord.getUpdateAt()));

        userCreate = UserBasicResponse.builder()
                .userId(flashCardWord.getUserCreate().getUserId())
                .name(flashCardWord.getUserCreate().getName())
                .build();

        userUpdate = UserBasicResponse.builder()
                .userId(flashCardWord.getUserUpdate().getUserId())
                .name(flashCardWord.getUserUpdate().getName())
                .build();

    }

}
