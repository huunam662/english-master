package com.example.englishmaster_be.DTO.invalidToken;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateInvalidTokenDTO {

    String token;

    Date expireTime;
}
