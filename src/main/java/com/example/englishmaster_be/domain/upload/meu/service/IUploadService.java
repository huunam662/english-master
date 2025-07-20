package com.example.englishmaster_be.domain.upload.meu.service;

import com.example.englishmaster_be.domain.upload.meu.dto.req.FileDeleteReq;
import com.example.englishmaster_be.common.dto.res.FileRes;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.UUID;

public interface IUploadService {

    FileRes upload(MultipartFile file, String dir, boolean isPrivateFile, UUID topicId, String code);

    FileRes upload(MultipartFile file, String dir, boolean isPrivateFile);

    FileRes upload(MultipartFile file);

    void delete(FileDeleteReq dto) throws FileNotFoundException;

    void delete(String filepath) throws FileNotFoundException;
}
