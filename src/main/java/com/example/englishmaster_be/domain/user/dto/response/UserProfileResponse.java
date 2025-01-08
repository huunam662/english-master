package com.example.englishmaster_be.domain.user.dto.response;


import com.example.englishmaster_be.common.constant.RoleEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileResponse {

    @Enumerated(EnumType.STRING)
    RoleEnum role;

    UserResponse user;
}
