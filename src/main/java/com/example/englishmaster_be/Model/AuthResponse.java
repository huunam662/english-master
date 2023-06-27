package com.example.englishmaster_be.Model;

public class AuthResponse {
    private String email;
    private String password;
    private Object role;
    private String accessToken;
    private String refreshToken;

    public AuthResponse(String email, String password, Object role, String accessToken, String refreshToken) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Object getRole() {
        return role;
    }

    public void setRole(Object role) {
        this.role = role;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
