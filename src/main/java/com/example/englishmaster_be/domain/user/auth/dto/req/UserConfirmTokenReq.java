package com.example.englishmaster_be.domain.user.auth.dto.req;

import com.example.englishmaster_be.domain.user.user.model.UserEntity;
import lombok.*;

@Data
@NoArgsConstructor
public class UserConfirmTokenReq {

    private UserEntity user;

}
