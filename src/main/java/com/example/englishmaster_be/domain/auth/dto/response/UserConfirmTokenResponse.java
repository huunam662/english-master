package com.example.englishmaster_be.domain.auth.dto.response;

import com.example.englishmaster_be.model.user.UserEntity;
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
public class UserConfirmTokenResponse {

    UUID userConfirmTokenId;

    String type;

    String code;

    UserEntity user;

    LocalDateTime createAt;

}
