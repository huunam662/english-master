package com.example.englishmaster_be.domain.upload.service;

import com.example.englishmaster_be.domain.upload.dto.request.FileDeleteRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.UUID;

public interface IUploadService {

    String upload(MultipartFile file, String dir, boolean isPrivateFile, UUID topicId, String code);

    void delete(FileDeleteRequest dto) throws FileNotFoundException;

}
