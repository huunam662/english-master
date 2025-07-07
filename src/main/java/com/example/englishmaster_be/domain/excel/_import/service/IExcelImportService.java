package com.example.englishmaster_be.domain.excel._import.service;

import com.example.englishmaster_be.domain.excel._import.dto.res.ExcelPartIdsRes;
import com.example.englishmaster_be.domain.excel._import.dto.res.ExcelTopicPartIdsRes;
import com.example.englishmaster_be.domain.topic.dto.response.TopicKeyRes;
import org.apache.coyote.BadRequestException;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;

public interface IExcelImportService {

    ExcelPartIdsRes importAllPartsForTopicFromExcel(UUID topicId, MultipartFile file);

    TopicKeyRes importTopicFromExcel(MultipartFile file);

    TopicKeyRes importTopicFromExcel(MultipartFile file, String imageUrl, String audioUrl);

    ExcelTopicPartIdsRes importQuestionForTopicAnyPartFromExcel(UUID topicId, int part, MultipartFile file);

    ExcelTopicPartIdsRes importQuestionAtAllPartForTopicFromExcel(UUID topicId, MultipartFile file);

    List<ExcelTopicPartIdsRes> importAllQuestionsFromPartForMultipleTopic(List<MultipartFile> files) throws BadRequestException;
}
