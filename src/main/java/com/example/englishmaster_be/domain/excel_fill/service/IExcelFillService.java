package com.example.englishmaster_be.domain.excel_fill.service;

import com.example.englishmaster_be.common.constant.ImportExcelType;
import com.example.englishmaster_be.domain.excel_fill.dto.response.*;
import com.example.englishmaster_be.domain.topic.dto.response.TopicKeyResponse;
import com.example.englishmaster_be.domain.user.model.UserEntity;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IExcelFillService {

    ExcelTopicResponse importTopicExcel(MultipartFile file);

    ExcelTopicContentResponse readTopicContentFromExcel(MultipartFile file);

    ExcelTopicResponse importAllPartsForTopicExcel(UUID topicId, MultipartFile file);

    ExcelPartIdsResponse importAllPartsForTopicFromExcel(UUID topicId, MultipartFile file);

    TopicKeyResponse importTopicFromExcel(MultipartFile file);

    ExcelQuestionListResponse importQuestionReadingPart67Excel(UUID topicId, MultipartFile file, int part);

    ExcelTopicPartIdsResponse importReadingPart67FromExcel(UUID topicId, int part, MultipartFile file);

    ExcelQuestionListResponse importQuestionReadingPart5Excel(UUID topicId, MultipartFile file);

    ExcelTopicPartIdsResponse importReadingPart5FromExcel(UUID topicId, MultipartFile file);

    ExcelQuestionListResponse importQuestionListeningPart12Excel(UUID topicId, MultipartFile file, int part);

    ExcelTopicPartIdsResponse importListeningPart12FromExcel(UUID topicId, int part, MultipartFile file);

    ExcelQuestionListResponse importQuestionListeningPart34Excel(UUID topicId, MultipartFile file, int part);

    ExcelTopicPartIdsResponse importListeningPart34FromExcel(UUID topicId, int part, MultipartFile file);

    List<ExcelQuestionResponse> importQuestionForTopicAndPart(UUID topicId, int partNumber, MultipartFile file);

    ExcelTopicPartIdsResponse importQuestionForTopicAnyPartFromExcel(UUID topicId, int part, MultipartFile file);

    ExcelTopicPartIdsResponse importQuestionAtAllPartForTopicFromExcel(UUID topicId, MultipartFile file, ImportExcelType typeImport);

    ExcelQuestionListResponse importQuestionAllPartsExcel(UUID topicId, MultipartFile file);

    TopicKeyResponse importTopicPartsQuestionsAnswersFunnyTest(MultipartFile file);
}
