package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Model.*;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.UUID;

@Getter
@Setter
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
        this.flashCardImage = flashCard.getFlashCardImage();

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

}
