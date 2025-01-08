package com.example.englishmaster_be.domain.excel_fill.service;

import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelQuestionListResponse;
import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelTopicResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface IExcelFillService {

    ExcelTopicResponse parseCreateTopicDTO(MultipartFile file) throws IOException;

    ExcelQuestionListResponse importQuestionReadingPart67Excel(UUID topicId, MultipartFile file, int part) throws IOException;

    ExcelQuestionListResponse importQuestionReadingPart5Excel(UUID topicId, MultipartFile file) throws IOException;

    ExcelQuestionListResponse importQuestionListeningPart12Excel(UUID topicId, MultipartFile file, int part) throws IOException;

    ExcelQuestionListResponse importQuestionListeningPart34Excel(UUID topicId, MultipartFile file, int part) throws IOException;

    ExcelQuestionListResponse importQuestionAllPartsExcel(UUID topicId, MultipartFile file) throws IOException;

}
