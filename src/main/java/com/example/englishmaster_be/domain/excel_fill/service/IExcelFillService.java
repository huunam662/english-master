package com.example.englishmaster_be.domain.excel_fill.service;

import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelQuestionListResponse;
import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelTopicResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface IExcelFillService {

    ExcelTopicResponse parseCreateTopicDTO(MultipartFile file) throws IOException;

    ExcelQuestionListResponse parseReadingPart67DTO(UUID topicId, MultipartFile file, int part) throws IOException;

    ExcelQuestionListResponse parseReadingPart5DTO(UUID topicId, MultipartFile file) throws IOException;

    ExcelQuestionListResponse parseListeningPart12DTO(UUID topicId, MultipartFile file, int part) throws IOException;

    ExcelQuestionListResponse parseListeningPart34DTO(UUID topicId, MultipartFile file, int part) throws IOException;

    ExcelQuestionListResponse parseAllPartsDTO(UUID topicId, MultipartFile file) throws IOException;

}
