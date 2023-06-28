package com.example.englishmaster_be.DTO;

public class RefreshTokenDTO {
    private String requestRefresh;

    public RefreshTokenDTO() {
    }

    public String getRequestRefresh() {
        return requestRefresh;
    }

    public void setRequestRefresh(String requestRefresh) {
        this.requestRefresh = requestRefresh;
    }
}
