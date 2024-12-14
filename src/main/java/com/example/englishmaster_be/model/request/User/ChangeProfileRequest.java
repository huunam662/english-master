package com.example.englishmaster_be.model.request.User;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangeProfileRequest {

    String name;

    String address;

    String phone;

    MultipartFile avatar;

}
