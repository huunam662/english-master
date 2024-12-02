package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Model.*;
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
public class FlashCardResponse {

    UUID flashCardId;

    String flashCardTitle;

    String flashCardImage;

    String flashCardDescription;

    String createAt;

    String updateAt;

    UserBasicResponse user;

    UserBasicResponse userCreate;

    UserBasicResponse userUpdate;

    public FlashCardResponse(FlashCard flashCard) {

        if(Objects.isNull(flashCard)) return;

        this.flashCardImage = flashCard.getFlashCardImage();
        this.flashCardId = flashCard.getFlashCardId();
        this.flashCardTitle = flashCard.getFlashCardTitle();
        this.flashCardDescription = flashCard.getFlashCardDescription();

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
        this.createAt = sdf.format(Timestamp.valueOf(flashCard.getCreateAt()));
        this.updateAt = sdf.format(Timestamp.valueOf(flashCard.getUpdateAt()));

        user = UserBasicResponse.builder()
                .userId(flashCard.getUser().getUserId())
                .name(flashCard.getUser().getName())
                .build();

        userCreate = UserBasicResponse.builder()
                .userId(flashCard.getUserCreate().getUserId())
                .name(flashCard.getUserCreate().getName())
                .build();

        userUpdate = UserBasicResponse.builder()
                .userId(flashCard.getUserUpdate().getUserId())
                .name(flashCard.getUserUpdate().getName())
                .build();
    }

}
