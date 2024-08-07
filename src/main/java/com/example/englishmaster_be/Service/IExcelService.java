package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.DTO.Topic.CreateTopicByExcelFileDTO;
import com.example.englishmaster_be.DTO.Topic.CreateTopicDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IExcelService {

    CreateTopicByExcelFileDTO parseCreateTopicDTO(MultipartFile file) throws IOException;
}
