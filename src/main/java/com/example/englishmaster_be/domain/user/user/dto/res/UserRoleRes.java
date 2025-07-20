package com.example.englishmaster_be.domain.user.user.dto.res;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class UserRoleRes extends UserRes{

    RoleRes role;
}
