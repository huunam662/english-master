package com.example.englishmaster_be.model.response;

import com.example.englishmaster_be.model.*;
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
public class FlashCardResponse {

    UUID flashCardId;

    String flashCardTitle;

    String flashCardImage;

    String flashCardDescription;

    String createAt;

    String updateAt;

    JSONObject user;

    JSONObject userCreate;

    JSONObject userUpdate;

    public FlashCardResponse(FlashCard flashCard) {

        if(Objects.isNull(flashCard)) return;

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
