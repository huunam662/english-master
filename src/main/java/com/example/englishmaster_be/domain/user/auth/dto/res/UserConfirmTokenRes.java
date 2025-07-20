package com.example.englishmaster_be.domain.user.auth.dto.res;

import com.example.englishmaster_be.domain.user.user.model.UserEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
@NoArgsConstructor
public class UserConfirmTokenRes {

    private UUID userConfirmTokenId;
    private String type;
    private String code;
    private UserEntity user;
    private LocalDateTime createAt;

}
