package com.example.englishmaster_be.domain.user.dto.response;


import com.example.englishmaster_be.common.constant.Role;
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
public class UserProfileRes {

    @Enumerated(EnumType.STRING)
    Role role;

    UserRes user;
}
