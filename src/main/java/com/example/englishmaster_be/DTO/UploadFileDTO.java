package com.example.englishmaster_be.DTO;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;


public class UploadFileDTO {
    private UUID id;
    private MultipartFile contentData;
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public MultipartFile getContentData() {
		return contentData;
	}
	public void setContentData(MultipartFile contentData) {
		this.contentData = contentData;
	}
    
    
    
}
