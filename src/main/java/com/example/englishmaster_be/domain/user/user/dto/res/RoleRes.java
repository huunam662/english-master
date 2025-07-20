package com.example.englishmaster_be.domain.user.user.dto.res;

import com.example.englishmaster_be.common.constant.Role;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class RoleRes {

    private UUID roleId;

    @Enumerated(EnumType.STRING)
    private Role roleName;

}
