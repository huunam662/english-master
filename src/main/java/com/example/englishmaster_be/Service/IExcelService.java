package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.DTO.Topic.CreateListQuestionByExcelFileDTO;
import com.example.englishmaster_be.DTO.Topic.CreateTopicByExcelFileDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IExcelService {

    CreateTopicByExcelFileDTO parseCreateTopicDTO(MultipartFile file) throws IOException;

    CreateListQuestionByExcelFileDTO parseReadingPart67DTO(MultipartFile file, int part) throws IOException;

    CreateListQuestionByExcelFileDTO parseReadingPart5DTO(MultipartFile file) throws IOException;

    CreateListQuestionByExcelFileDTO parseListeningPart12DTO(MultipartFile file, int part) throws IOException;

    CreateListQuestionByExcelFileDTO parseListeningPart34DTO(MultipartFile file, int part) throws IOException;

    CreateListQuestionByExcelFileDTO parseAllPartsDTO(MultipartFile file) throws IOException;

    boolean isExcelFile(MultipartFile file);


}
