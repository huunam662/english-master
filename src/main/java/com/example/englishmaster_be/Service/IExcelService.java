package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.DTO.topic.CreateListQuestionByExcelFileDTO;
import com.example.englishmaster_be.DTO.topic.CreateTopicByExcelFileDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface IExcelService {

    CreateTopicByExcelFileDTO parseCreateTopicDTO(MultipartFile file) throws IOException;

    CreateListQuestionByExcelFileDTO parseReadingPart67DTO(UUID topicId, MultipartFile file, int part) throws IOException;

    CreateListQuestionByExcelFileDTO parseReadingPart5DTO(UUID topicId, MultipartFile file) throws IOException;

    CreateListQuestionByExcelFileDTO parseListeningPart12DTO(UUID topicId, MultipartFile file, int part) throws IOException;

    CreateListQuestionByExcelFileDTO parseListeningPart34DTO(UUID topicId, MultipartFile file, int part) throws IOException;

    CreateListQuestionByExcelFileDTO parseAllPartsDTO(UUID topicId, MultipartFile file) throws IOException;

    boolean isExcelFile(MultipartFile file);


}
