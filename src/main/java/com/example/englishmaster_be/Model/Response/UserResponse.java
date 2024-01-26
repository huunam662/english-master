package com.example.englishmaster_be.Model.Response;

import com.example.englishmaster_be.Helper.GetExtension;
import com.example.englishmaster_be.Model.User;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.UUID;

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
        if(user.getAvatar() == null){
            this.avatar = null;
        }else {
            String link = GetExtension.linkName(user.getAvatar());
            this.avatar = link + user.getAvatar();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
