package com.example.englishmaster_be.domain.upload.meu.service;

import com.example.englishmaster_be.domain.upload.meu.request.FileDeleteRequest;
import com.example.englishmaster_be.common.dto.response.FileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.UUID;

public interface IUploadService {

    FileResponse upload(MultipartFile file, String dir, boolean isPrivateFile, UUID topicId, String code);

    FileResponse upload(MultipartFile file, String dir, boolean isPrivateFile);

    FileResponse upload(MultipartFile file);

    void delete(FileDeleteRequest dto) throws FileNotFoundException;

    void delete(String filepath) throws FileNotFoundException;
}
