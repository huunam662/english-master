package com.example.englishmaster_be.domain.user.user.dto.res;


import com.example.englishmaster_be.common.constant.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Data
@NoArgsConstructor
public class UserProfileRes {

    @Enumerated(EnumType.STRING)
    private Role role;

    private UserRes user;
}
