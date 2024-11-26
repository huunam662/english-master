package com.example.englishmaster_be.dto;


import java.util.UUID;

public class UploadTextDTO {
    private String contentType;
    private String contentData;

	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getContentData() {
		return contentData;
	}
	public void setContentData(String contentData) {
		this.contentData = contentData;
	}
    
    
}
