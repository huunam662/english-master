package com.example.englishmaster_be.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;


public class UploadFileDTO {
    private MultipartFile contentData;
	public MultipartFile getContentData() {
		return contentData;
	}
	public void setContentData(MultipartFile contentData) {
		this.contentData = contentData;
	}
    
    
    
}
