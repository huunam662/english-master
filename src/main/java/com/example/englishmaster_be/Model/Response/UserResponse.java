package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Model.User;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.UUID;

public class UserResponse {
    private UUID userId;
    private String userName;
    private String phone;
    private String address;
    private String avatar;

    private String createAt;
    private String updateAt;

    public UserResponse(User user) {
        this.userId = user.getUserId();
        this.userName = user.getName();
        this.phone = user.getPhone();
        this.address = user.getAddress();
        this.avatar = user.getAvatar();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        this.createAt = sdf.format(Timestamp.valueOf(user.getCreateAt()));
        this.updateAt = sdf.format(Timestamp.valueOf(user.getUpdateAt()));
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }
}
