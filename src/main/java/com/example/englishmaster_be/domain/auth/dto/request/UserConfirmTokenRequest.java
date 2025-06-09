package com.example.englishmaster_be.domain.auth.dto.request;

import com.example.englishmaster_be.domain.user.model.UserEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserConfirmTokenRequest {

    UserEntity user;

}
