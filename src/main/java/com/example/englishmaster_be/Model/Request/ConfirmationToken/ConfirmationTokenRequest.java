package com.example.englishmaster_be.Model.Request.ConfirmationToken;

import com.example.englishmaster_be.entity.UserEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfirmationTokenRequest {

    UserEntity user;

}
