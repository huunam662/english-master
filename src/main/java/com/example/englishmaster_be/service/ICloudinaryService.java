package com.example.englishmaster_be.service;

import com.example.englishmaster_be.model.response.CloudiaryUploadFileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ICloudinaryService {

    CloudiaryUploadFileResponse uploadFile(MultipartFile file);

}
