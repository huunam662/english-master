package com.example.englishmaster_be.domain.user.user.dto.res;


import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.UUID;

@Data
@NoArgsConstructor
public class UserRes {

    private UUID userId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String avatar;
    private String userType;

}
