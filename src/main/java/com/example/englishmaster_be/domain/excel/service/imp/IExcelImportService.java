package com.example.englishmaster_be.domain.excel.service.imp;

import com.example.englishmaster_be.common.constant.TopicType;
import com.example.englishmaster_be.domain.excel.dto.response.*;
import com.example.englishmaster_be.domain.topic.dto.response.TopicKeyResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface IExcelImportService {

    ExcelPartIdsResponse importAllPartsForTopicFromExcel(UUID topicId, MultipartFile file);

    TopicKeyResponse importTopicFromExcel(MultipartFile file);

    ExcelTopicPartIdsResponse importReadingPart67FromExcel(UUID topicId, int part, MultipartFile file);

    ExcelTopicPartIdsResponse importReadingPart5FromExcel(UUID topicId, MultipartFile file);

    ExcelTopicPartIdsResponse importListeningPart12FromExcel(UUID topicId, int part, MultipartFile file);

    ExcelTopicPartIdsResponse importListeningPart34FromExcel(UUID topicId, int part, MultipartFile file);

    ExcelTopicPartIdsResponse importQuestionForTopicAnyPartFromExcel(UUID topicId, int part, MultipartFile file);

    TopicKeyResponse importTopicPartsQuestionsAnswersReading(MultipartFile file);

    ExcelTopicPartIdsResponse importSpeakingAllPartsForTopicFromExcel(UUID topicId, MultipartFile file);

    ExcelTopicPartIdsResponse importQuestionAtAllPartForTopicFromExcel(UUID topicId, MultipartFile file, TopicType typeImport);

}
