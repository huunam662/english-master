package com.example.englishmaster_be.DTO;


import java.util.UUID;

public class UploadTextDTO {
    private UUID id;
    private String contentType;
    private String contentData;
    
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
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
