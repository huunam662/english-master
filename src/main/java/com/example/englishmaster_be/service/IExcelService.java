package com.example.englishmaster_be.service;

import com.example.englishmaster_be.model.response.excel.ListQuestionByExcelFileResponse;
import com.example.englishmaster_be.model.response.excel.TopicByExcelFileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface IExcelService {

    TopicByExcelFileResponse parseCreateTopicDTO(MultipartFile file) throws IOException;

    ListQuestionByExcelFileResponse parseReadingPart67DTO(UUID topicId, MultipartFile file, int part) throws IOException;

    ListQuestionByExcelFileResponse parseReadingPart5DTO(UUID topicId, MultipartFile file) throws IOException;

    ListQuestionByExcelFileResponse parseListeningPart12DTO(UUID topicId, MultipartFile file, int part) throws IOException;

    ListQuestionByExcelFileResponse parseListeningPart34DTO(UUID topicId, MultipartFile file, int part) throws IOException;

    ListQuestionByExcelFileResponse parseAllPartsDTO(UUID topicId, MultipartFile file) throws IOException;

}
