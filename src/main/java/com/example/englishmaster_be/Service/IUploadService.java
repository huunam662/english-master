package com.example.englishmaster_be.Service;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface IUploadService {
    String upload(MultipartFile file, String dir, boolean isPrivateFile, UUID topicId, String code);
    

}
