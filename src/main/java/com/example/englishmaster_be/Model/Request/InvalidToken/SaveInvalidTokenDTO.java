package com.example.englishmaster_be.Model.Request.InvalidToken;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SaveInvalidTokenDTO {

    String token;

    Date expireTime;
}
