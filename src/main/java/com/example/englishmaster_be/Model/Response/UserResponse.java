package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Model.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {

    UUID userId;

    String name;

    String email;

    String phone;

    String address;

    String avatar;

    String createAt;

    String updateAt;

    boolean enable;


    public UserResponse(User user) {

        if(Objects.isNull(user)) return;

        this.userId = user.getUserId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.address = user.getAddress();
        this.enable = user.isEnabled();

        if(Objects.nonNull(user.getAvatar()))
            this.avatar = user.getAvatar();

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
        if(Objects.nonNull(user.getCreateAt()))
            this.createAt = sdf.format(Timestamp.valueOf(user.getCreateAt()));
        if(Objects.nonNull(user.getUpdateAt()))
            this.updateAt = sdf.format(Timestamp.valueOf(user.getUpdateAt()));
    }

}
