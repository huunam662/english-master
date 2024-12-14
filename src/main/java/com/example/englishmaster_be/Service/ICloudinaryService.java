package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Model.Response.CloudiaryUploadFileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ICloudinaryService {

    CloudiaryUploadFileResponse uploadFile(MultipartFile file);

}
