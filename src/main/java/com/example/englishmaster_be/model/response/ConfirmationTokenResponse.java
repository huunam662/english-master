package com.example.englishmaster_be.model.response;

import com.example.englishmaster_be.model.User;
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

    User user;

    LocalDateTime createAt;

}
