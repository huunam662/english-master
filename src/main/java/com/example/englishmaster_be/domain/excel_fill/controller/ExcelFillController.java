package com.example.englishmaster_be.domain.excel_fill.controller;


import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelQuestionListResponse;
import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelQuestionResponse;
import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelTopicResponse;
import com.example.englishmaster_be.domain.excel_fill.service.IExcelFillService;
import com.example.englishmaster_be.domain.topic.dto.response.TopicKeyResponse;
import com.example.englishmaster_be.domain.excel_fill.mapper.ExcelContentMapper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = "Excel")
@RestController
@RequestMapping("/excel")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExcelFillController {

    IExcelFillService excelService;

    @PostMapping(value = "/importTopicInformation", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @DefaultMessage("File processed successfully")
    @SneakyThrows
    public ExcelTopicResponse importTopicInformation(@RequestPart("file") MultipartFile file) {

        return excelService.importTopicExcel(file);
    }

    @PostMapping(value = "/importQuestionForTopicAndPart", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @DefaultMessage("File processed successfully")
    public ExcelQuestionListResponse importQuestionForTopicAndPart(
            @RequestParam("topicId") UUID topicId,
            @Parameter(description = "Part number must one value in scope [1, 2, 3, 4, 5, 6, 7]")
            @RequestParam(value = "partNumber") int partNumber,
            @RequestPart("file") MultipartFile file
    ){

        List<ExcelQuestionResponse> excelQuestionResponses = excelService.importQuestionForTopicAndPart(topicId, partNumber, file);

        return ExcelContentMapper.INSTANCE.toExcelQuestionListResponse(excelQuestionResponses);
    }

    @PostMapping(value = "/importAllPartsForTopic", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @DefaultMessage("File processed successfully")
    public ExcelTopicResponse importAllPartsForTopic(
            @RequestParam("topicId") UUID topicId,
            @RequestPart("file") MultipartFile file
    ){

        return excelService.importAllPartsForTopicExcel(topicId, file);
    }


    @PostMapping(value = "/importQuestionAllPartsForTopic", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @DefaultMessage("File processed successfully")
    @SneakyThrows
    public ExcelQuestionListResponse importQuestionAllPartsForTopic(
            @RequestParam("topicId") UUID topicId,
            @RequestPart("file") MultipartFile file
    ) {

        return excelService.importQuestionAllPartsExcel(topicId, file);
    }

    @PostMapping(value = "/import/exam/funny-intern-test", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @DefaultMessage("File processes successful.")
    @SneakyThrows
    public TopicKeyResponse importTopicPartsQuestionsAnswersFunnyTest(
            @RequestPart("file") MultipartFile file
    ){

        return excelService.importTopicPartsQuestionsAnswersFunnyTest(file);
    }
}
