package com.example.englishmaster_be.DTO;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public class UploadMultiFileDTO {
    private MultipartFile[] contentData;
	public MultipartFile[] getContentData() {
		return contentData;
	}
	public void setContentData(MultipartFile[] contentData) {
		this.contentData = contentData;
	}
    
    
}
