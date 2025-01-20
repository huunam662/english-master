package com.example.englishmaster_be.domain.excel_fill.service;

import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelQuestionListResponse;
import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelQuestionResponse;
import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelTopicContentResponse;
import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelTopicResponse;
import com.example.englishmaster_be.model.topic.TopicEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface IExcelFillService {

    ExcelTopicResponse importTopicExcel(MultipartFile file);

    ExcelTopicResponse importAllPartsForTopicExcel(UUID topicId, MultipartFile file);

    ExcelTopicContentResponse readTopicContentFromExcel(MultipartFile file);

    ExcelQuestionListResponse importQuestionReadingPart67Excel(UUID topicId, MultipartFile file, int part);

    ExcelQuestionListResponse importQuestionReadingPart5Excel(UUID topicId, MultipartFile file);

    ExcelQuestionListResponse importQuestionListeningPart12Excel(UUID topicId, MultipartFile file, int part);

    ExcelQuestionListResponse importQuestionListeningPart34Excel(UUID topicId, MultipartFile file, int part);

    ExcelQuestionListResponse importQuestionFillInBlankPart8Excel(UUID topicId, MultipartFile file);

    ExcelQuestionListResponse importQuestionMatchingWordsPart9Excel(UUID topicId, MultipartFile file);

    List<ExcelQuestionResponse> importQuestionForTopicAndPart(UUID topicId, int partNumber, MultipartFile file);

    ExcelQuestionListResponse importQuestionAllPartsExcel(UUID topicId, MultipartFile file);

}
