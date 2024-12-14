package com.example.englishmaster_be.model.response;

import com.example.englishmaster_be.entity.UserEntity;
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
public class ConfirmationTokenResponse {

    UUID userConfirmTokenId;

    String type;

    String code;

    UserEntity user;

    LocalDateTime createAt;

}
