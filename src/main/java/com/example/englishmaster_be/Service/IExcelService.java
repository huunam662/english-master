package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.DTO.Topic.CreateListQuestionByExcelFileDTO;
import com.example.englishmaster_be.DTO.Topic.CreateListQuestionDTO;
import com.example.englishmaster_be.DTO.Topic.CreateTopicByExcelFileDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IExcelService {

    CreateTopicByExcelFileDTO parseCreateTopicDTO(MultipartFile file) throws IOException;

    CreateListQuestionByExcelFileDTO parseReadingPartDTO (MultipartFile file) throws IOException;

    CreateListQuestionByExcelFileDTO parseCreateListQuestionDTO(MultipartFile file) throws IOException;
}
