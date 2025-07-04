package com.example.englishmaster_be.domain.user.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRes {

    UUID userId;

    String name;

    String email;

    String phone;

    String address;

    String avatar;

}
