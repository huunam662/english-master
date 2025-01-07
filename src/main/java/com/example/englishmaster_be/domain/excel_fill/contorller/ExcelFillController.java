package com.example.englishmaster_be.domain.excel_fill.contorller;


import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelQuestionListResponse;
import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelTopicResponse;
import com.example.englishmaster_be.domain.excel_fill.service.IExcelFillService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Tag(name = "Excel")
@RestController
@RequestMapping("/excel")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExcelFillController {

    IExcelFillService excelService;

    @PostMapping(value = "/importExcel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @DefaultMessage("File processed successfully")
    @SneakyThrows
    public ExcelTopicResponse getCreateTopicByExcelFileDTO(@RequestPart("file") MultipartFile file) {

        return excelService.parseCreateTopicDTO(file);
    }

    @PostMapping(value = "/importQuestionPart67", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @DefaultMessage("File processed successfully")
    @SneakyThrows
    public ExcelQuestionListResponse getCreateQuestionPart67ByExcelFileDTO(
            @RequestParam("topicId") UUID topicId,
            @RequestParam("part") int part,
            @RequestPart("file") MultipartFile file
    ) {

        return excelService.importQuestionReadingPart67Excel(topicId, file, part);
    }

    @PostMapping(value = "/importQuestionPart5", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @DefaultMessage("File processed successfully")
    @SneakyThrows
    public ExcelQuestionListResponse getCreateQuestionByExcelFileDTO(
            @RequestParam("topicId") UUID topicId,
            @RequestPart("file") MultipartFile file
    ) {

        return excelService.importQuestionReadingPart5Excel(topicId, file);
    }

    @PostMapping(value = "/importQuestionPart12", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @DefaultMessage("File processed successfully")
    @SneakyThrows
    public ExcelQuestionListResponse getCreateQuestionPart12ByExcelFileDTO(
            @RequestParam("topicId") UUID topicId,
            @RequestParam("part") int part,
            @RequestPart("file") MultipartFile file
    ) {

        return excelService.importQuestionListeningPart12Excel(topicId, file, part);
    }


    @PostMapping(value = "/importQuestionPart34", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @DefaultMessage("File processed successfully")
    @SneakyThrows
    public ExcelQuestionListResponse getCreateQuestionPart34ByExcelFileDTO(
            @RequestParam("topicId") UUID topicId,
            @RequestParam("part") int part,
            @RequestPart("file") MultipartFile file
    ) {

        return excelService.importQuestionListeningPart34Excel(topicId, file, part);
    }

    @PostMapping(value = "/importAllParts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @DefaultMessage("File processed successfully")
    @SneakyThrows
    public ExcelQuestionListResponse getCreateQuestionAllPartByExcelFileDTO(
            @RequestParam("topicId") UUID topicId,
            @RequestPart("file") MultipartFile file
    ) {

        return excelService.importQuestionAllPartsExcel(topicId, file);
    }
}
