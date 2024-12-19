package com.example.englishmaster_be.domain.cloudinary.service;

import com.example.englishmaster_be.domain.cloudinary.dto.response.CloudiaryUploadFileResponse;
import com.example.englishmaster_be.domain.file_storage.dto.response.FileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ICloudinaryService {

    FileResponse uploadFile(MultipartFile file);

}
