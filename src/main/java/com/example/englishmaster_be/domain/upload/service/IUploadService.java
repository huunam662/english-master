package com.example.englishmaster_be.domain.upload.service;

import com.example.englishmaster_be.domain.file_storage.dto.response.FileResponse;
import com.example.englishmaster_be.domain.upload.dto.request.FileDeleteRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.UUID;

public interface IUploadService {

    FileResponse upload(MultipartFile file, String dir, boolean isPrivateFile, UUID topicId, String code);

    FileResponse upload(MultipartFile file, String dir, boolean isPrivateFile);

    FileResponse upload(MultipartFile file);

    void delete(FileDeleteRequest dto) throws FileNotFoundException;

}
