package com.example.englishmaster_be.domain.cloudinary.service;

import com.example.englishmaster_be.domain.cloudinary.dto.response.CloudiaryUploadFileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ICloudinaryService {

    CloudiaryUploadFileResponse uploadFile(MultipartFile file);

}
