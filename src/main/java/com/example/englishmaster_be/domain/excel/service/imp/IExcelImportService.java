package com.example.englishmaster_be.domain.excel.service.imp;

import com.example.englishmaster_be.domain.excel.dto.response.*;
import com.example.englishmaster_be.domain.topic.dto.response.TopicKeyRes;
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface IExcelImportService {

    ExcelPartIdsResponse importAllPartsForTopicFromExcel(UUID topicId, MultipartFile file);

    TopicKeyRes importTopicFromExcel(MultipartFile file);

    TopicKeyRes importTopicFromExcel(MultipartFile file, String imageUrl, String audioUrl);

    ExcelTopicPartIdsResponse importReadingPart67FromExcel(UUID topicId, int part, MultipartFile file);

    ExcelTopicPartIdsResponse importReadingPart5FromExcel(UUID topicId, MultipartFile file);

    ExcelTopicPartIdsResponse importListeningPart12FromExcel(UUID topicId, int part, MultipartFile file);

    ExcelTopicPartIdsResponse importListeningPart34FromExcel(UUID topicId, int part, MultipartFile file);

    ExcelTopicPartIdsResponse importQuestionForTopicAnyPartFromExcel(UUID topicId, int part, MultipartFile file);

    TopicKeyRes importTopicPartsQuestionsAnswersReading(MultipartFile file);

    ExcelTopicPartIdsResponse importSpeakingAllPartsForTopicFromExcel(UUID topicId, MultipartFile file);

    ExcelTopicPartIdsResponse importQuestionAtAllPartForTopicFromExcel(UUID topicId, MultipartFile file);

    List<ExcelTopicPartIdsResponse> importAllQuestionsFromPartForMultipleTopic(List<MultipartFile> files) throws BadRequestException;
}
