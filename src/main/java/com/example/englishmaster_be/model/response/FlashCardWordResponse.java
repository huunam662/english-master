package com.example.englishmaster_be.model.response;

import com.example.englishmaster_be.model.FlashCardWord;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.json.simple.JSONObject;


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

    JSONObject userCreate;

    JSONObject userUpdate;

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

        userCreate = new JSONObject();
        userUpdate = new JSONObject();

        userCreate.put("User Id", flashCardWord.getUserCreate().getUserId());
        userCreate.put("User Name", flashCardWord.getUserCreate().getName());

        userUpdate.put("User Id", flashCardWord.getUserUpdate().getUserId());
        userUpdate.put("User Name", flashCardWord.getUserUpdate().getName());
    }

}
