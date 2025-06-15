package com.example.englishmaster_be.domain.excel.controller.imp;


import com.example.englishmaster_be.domain.excel.dto.response.*;
import com.example.englishmaster_be.domain.excel.service.imp.IExcelImportService;
import com.example.englishmaster_be.domain.topic.dto.response.TopicKeyResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Tag(name = "Excel Import")
@RestController
@RequestMapping("/excel")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExcelImportController {

    IExcelImportService excelService;

    @PostMapping(value = "/import/topic-information", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SneakyThrows
    @Operation(
            summary = "Import topic information from excel file.",
            description = "Import topic information from excel file."
    )
    public TopicKeyResponse importTopicInformationFromExcel(@RequestPart("file") MultipartFile file) {

        return excelService.importTopicFromExcel(file);
    }


    @PostMapping(value = "/import/{topicId:.+}/questions-to-topic-part", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SneakyThrows
    @Operation(
            summary = "Import questions for topic and part.",
            description = "Import questions for topic and part."
    )
    public ExcelTopicPartIdsResponse importQuestionsForTopicAndPart(
            @PathVariable("topicId") UUID topicId,
            @Parameter(description = "Part number must one value in scope [1, 2, 3, 4, 5, 6, 7]")
            @RequestParam("partNumber") int partNumber,
            @RequestPart("file") MultipartFile file
    ){

        return excelService.importQuestionForTopicAnyPartFromExcel(topicId, partNumber, file);
    }


    @PostMapping(value = "/import/{topicId:.+}/parts-to-topic", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SneakyThrows
    @Operation(
            summary = "Import all parts for topic from excel file.",
            description = "Import all parts for topic from excel file."
    )
    public ExcelPartIdsResponse importPartsForTopicFromExcel(
            @PathVariable("topicId") UUID topicId,
            @RequestPart("file") MultipartFile file
    ){

        return excelService.importAllPartsForTopicFromExcel(topicId, file);
    }


    @PostMapping(value = "/import/{topicId:.+}/all-questions-to-topic-part", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SneakyThrows
    @Operation(
            summary = "Import all questions from parts for topic from excel file.",
            description = "Import all questions from parts for topic from excel file."
    )
    public ExcelPartIdsResponse importAllQuestionsFromPartForTopicFromExcel(
            @PathVariable("topicId") UUID topicId,
            @RequestPart("file") MultipartFile file
    ){

        return excelService.importQuestionAtAllPartForTopicFromExcel(topicId, file);
    }
}
