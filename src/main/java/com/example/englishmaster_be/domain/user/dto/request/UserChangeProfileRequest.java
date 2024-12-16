package com.example.englishmaster_be.domain.user.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserChangeProfileRequest {

    String name;

    String address;

    String phone;

    MultipartFile avatar;

}
