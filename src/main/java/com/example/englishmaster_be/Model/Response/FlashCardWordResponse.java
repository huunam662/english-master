package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Component.GetExtension;
import com.example.englishmaster_be.Model.FlashCard;
import com.example.englishmaster_be.Model.FlashCardWord;
import com.example.englishmaster_be.Model.Topic;
import com.example.englishmaster_be.Model.User;
import org.json.simple.JSONObject;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.UUID;

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

        String link;
        if(flashCardWord.getImage() == null){
            this.image = flashCardWord.getImage();

        }else {

            link = GetExtension.linkName(flashCardWord.getImage());
            this.image = link + flashCardWord.getImage();
        }

        this.wordId = flashCardWord.getWordId();
        this.flashCardId = flashCardWord.getFlashCard().getFlashCardId();
        this.word = flashCardWord.getWord();
        this.type = flashCardWord.getType();
        this.spelling = flashCardWord.getSpelling();
        this.example = flashCardWord.getExample();
        this.note = flashCardWord.getNote();
        this.define = flashCardWord.getDefine();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        this.createAt = sdf.format(Timestamp.valueOf(flashCardWord.getCreateAt()));
        this.updateAt = sdf.format(Timestamp.valueOf(flashCardWord.getUpdateAt()));

        userCreate = new JSONObject();
        userUpdate = new JSONObject();

        userCreate.put("User Id", flashCardWord.getUserCreate().getUserId());
        userCreate.put("User Name", flashCardWord.getUserCreate().getName());

        userUpdate.put("User Id", flashCardWord.getUserUpdate().getUserId());
        userUpdate.put("User Name", flashCardWord.getUserUpdate().getName());
    }

    public UUID getWordId() {
        return wordId;
    }

    public void setWordId(UUID wordId) {
        this.wordId = wordId;
    }

    public UUID getFlashCardId() {
        return flashCardId;
    }

    public void setFlashCardId(UUID flashCardId) {
        this.flashCardId = flashCardId;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSpelling() {
        return spelling;
    }

    public void setSpelling(String spelling) {
        this.spelling = spelling;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDefine() {
        return define;
    }

    public void setDefine(String define) {
        this.define = define;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

    public JSONObject getUserCreate() {
        return userCreate;
    }

    public void setUserCreate(JSONObject userCreate) {
        this.userCreate = userCreate;
    }

    public JSONObject getUserUpdate() {
        return userUpdate;
    }

    public void setUserUpdate(JSONObject userUpdate) {
        this.userUpdate = userUpdate;
    }
}
