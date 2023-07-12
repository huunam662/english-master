package com.example.englishmaster_be.DTO;



public class ChangePasswordDTO {
    private String code;
    private String newPass;
    private String confirmPass;

    public ChangePasswordDTO() {
    }

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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
