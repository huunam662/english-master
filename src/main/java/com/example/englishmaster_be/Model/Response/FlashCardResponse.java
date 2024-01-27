package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Helper.GetExtension;
import com.example.englishmaster_be.Model.*;
import org.json.simple.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.UUID;

public class FlashCardResponse {

    private UUID flashCardId;

    private JSONObject user;

    private String flashCardTitle;

    private String flashCardImage;

    private String flashCardDescription;

    private String createAt;

    private String updateAt;

    private JSONObject userCreate;

    private JSONObject userUpdate;

    public FlashCardResponse(FlashCard flashCard) {

        String link;
        if(flashCard.getFlashCardImage() == null){
            this.flashCardImage = flashCard.getFlashCardImage();

        }else {

            link = GetExtension.linkName(flashCard.getFlashCardImage());
            this.flashCardImage = link + flashCard.getFlashCardImage();
        }

        this.flashCardId = flashCard.getFlashCardId();
        this.flashCardTitle = flashCard.getFlashCardTitle();
        this.flashCardDescription = flashCard.getFlashCardDescription();

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
        this.createAt = sdf.format(Timestamp.valueOf(flashCard.getCreateAt()));
        this.updateAt = sdf.format(Timestamp.valueOf(flashCard.getUpdateAt()));

        userCreate = new JSONObject();
        userUpdate = new JSONObject();
        user = new JSONObject();

        user.put("User Id", flashCard.getUser().getUserId());
        user.put("User Name", flashCard.getUser().getName());

        userCreate.put("User Id", flashCard.getUserCreate().getUserId());
        userCreate.put("User Name", flashCard.getUserCreate().getName());

        userUpdate.put("User Id", flashCard.getUserUpdate().getUserId());
        userUpdate.put("User Name", flashCard.getUserUpdate().getName());
    }

    public UUID getFlashCardId() {
        return flashCardId;
    }

    public void setFlashCardId(UUID flashCardId) {
        this.flashCardId = flashCardId;
    }

    public JSONObject getUser() {
        return user;
    }

    public void setUser(JSONObject user) {
        this.user = user;
    }

    public String getFlashCardTitle() {
        return flashCardTitle;
    }

    public void setFlashCardTitle(String flashCardTitle) {
        this.flashCardTitle = flashCardTitle;
    }

    public String getFlashCardImage() {
        return flashCardImage;
    }

    public void setFlashCardImage(String flashCardImage) {
        this.flashCardImage = flashCardImage;
    }

    public String getFlashCardDescription() {
        return flashCardDescription;
    }

    public void setFlashCardDescription(String flashCardDescription) {
        this.flashCardDescription = flashCardDescription;
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
