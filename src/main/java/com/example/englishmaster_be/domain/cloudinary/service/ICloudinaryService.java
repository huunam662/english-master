package com.example.englishmaster_be.domain.cloudinary.service;

import com.example.englishmaster_be.shared.dto.response.FileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ICloudinaryService {

    FileResponse uploadFile(MultipartFile file);

    FileResponse uploadAudio(MultipartFile file);
}
