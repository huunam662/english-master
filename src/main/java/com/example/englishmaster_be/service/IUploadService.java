package com.example.englishmaster_be.service;

import com.example.englishmaster_be.dto.DeleteRequestDto;
import com.example.englishmaster_be.model.response.DeleteResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.UUID;

public interface IUploadService {

    String upload(MultipartFile file, String dir, boolean isPrivateFile, UUID topicId, String code);

    DeleteResponse delete(DeleteRequestDto dto) throws FileNotFoundException;

}
