package com.example.englishmaster_be.DTO.User;

import lombok.Getter;
import lombok.Setter;


public class ChangePassDTO {
    private String oldPass;
    private String newPass;
    private String confirmPass;

    public ChangePassDTO() {
    }

    public String getOldPass() {
        return oldPass;
    }

    public void setOldPass(String oldPass) {
        this.oldPass = oldPass;
    }

    public String getNewPass() {
        return newPass;
    }

    public void setNewPass(String newPass) {
        this.newPass = newPass;
    }

    public String getConfirmPass() {
        return confirmPass;
    }

    public void setConfirmPass(String confirmPass) {
        this.confirmPass = confirmPass;
    }
}
