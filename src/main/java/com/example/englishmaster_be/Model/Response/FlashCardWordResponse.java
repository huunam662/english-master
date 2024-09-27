package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Model.FlashCardWord;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.UUID;

@Getter
@Setter
public class FlashCardWordResponse {
    private UUID wordId;

    private UUID flashCardId;
    private String word;


    private String image;

    private String type;
    private String spelling;
    private String example;

    private String note;

    private String define;

    private String createAt;

    private String updateAt;

    private JSONObject userCreate;

    private JSONObject userUpdate;

    public FlashCardWordResponse(FlashCardWord flashCardWord) {
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
