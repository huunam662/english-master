package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Model.User;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.UUID;

@Getter
@Setter
public class UserResponse {
    private UUID userId;
    private String userName;
    private String email;
    private String phone;
    private String address;
    private String avatar;
    private boolean enable;

    private String createAt;
    private String updateAt;

    public UserResponse(User user) {
        this.userId = user.getUserId();
        this.userName = user.getName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.address = user.getAddress();
        this.enable = user.isEnabled();
        if (user.getAvatar() == null) {
            this.avatar = null;
        } else {
            this.avatar = user.getAvatar();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
        this.createAt = sdf.format(Timestamp.valueOf(user.getCreateAt()));
        this.updateAt = sdf.format(Timestamp.valueOf(user.getUpdateAt()));
    }

}
