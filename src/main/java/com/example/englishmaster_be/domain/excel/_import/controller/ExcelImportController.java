package com.example.englishmaster_be.domain.excel._import.controller;

<<<<<<< HEAD:src/main/java/com/example/englishmaster_be/domain/excel/_import/controller/ExcelImportController.java
import com.example.englishmaster_be.domain.excel._import.dto.res.ExcelPartIdsRes;
import com.example.englishmaster_be.domain.excel._import.dto.res.ExcelTopicPartIdsRes;
import com.example.englishmaster_be.domain.excel._import.service.IExcelImportService;
import com.example.englishmaster_be.domain.exam.topic.topic.dto.res.TopicKeyRes;
=======

import com.example.englishmaster_be.domain.excel.dto.response.*;
import com.example.englishmaster_be.domain.excel.service.imp.IExcelImportService;
import com.example.englishmaster_be.domain.topic.dto.response.TopicKeyResponse;
>>>>>>> 197ed81940903ab14a285d25c6aed6f94b8e649c:src/main/java/com/example/englishmaster_be/domain/excel/controller/imp/ExcelImportController.java
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;

@Tag(name = "Excel Import")
@RestController
@RequestMapping("/excel")
public class ExcelImportController {

    private final IExcelImportService excelService;

    public ExcelImportController(IExcelImportService excelService) {
        this.excelService = excelService;
    }

    @PostMapping(value = "/import/topic-information", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SneakyThrows
    @Operation(
            summary = "Import topic information from excel file.",
            description = "Import topic information from excel file."
    )
    public TopicKeyRes importTopicInformationFromExcel(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "imageUrl", required = false) String imageUrl,
            @RequestParam(value = "audioUrl", required = false) String audioUrl
    ) {

        return excelService.importTopicFromExcel(file, imageUrl, audioUrl);
    }


    @PostMapping(value = "/import/{topicId:.+}/questions-to-topic-part", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SneakyThrows
    @Operation(
            summary = "Import questions for topic and part.",
            description = "Import questions for topic and part."
    )
    public ExcelTopicPartIdsRes importQuestionsForTopicAndPart(
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
    public ExcelPartIdsRes importPartsForTopicFromExcel(
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
    public ExcelPartIdsRes importAllQuestionsFromPartForTopicFromExcel(
            @PathVariable("topicId") UUID topicId,
            @RequestPart("file") MultipartFile file
    ){

        return excelService.importQuestionAtAllPartForTopicFromExcel(topicId, file);
    }

    @PostMapping(value = "/import/multiple-topic/all-questions-to-topic-part", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SneakyThrows
    @Operation(
            summary = "Import all questions from parts for multiple topic from multiple excel file.",
            description = "Import all questions from parts for multiple topic from multiple excel file."
    )
    public List<ExcelTopicPartIdsRes> importAllQuestionsFromPartForMultipleTopic(
            @RequestPart("files") List<MultipartFile> files
    ){
        return excelService.importAllQuestionsFromPartForMultipleTopic(files);
    }

}
