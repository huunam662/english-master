package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Configuration.global.annotation.MessageResponse;
import com.example.englishmaster_be.Model.Response.ExceptionResponseModel;
import com.example.englishmaster_be.Model.Response.excel.CreateListQuestionByExcelFileResponse;
import com.example.englishmaster_be.Model.Response.excel.CreateTopicByExcelFileResponse;
import com.example.englishmaster_be.Model.Response.ResponseModel;
import com.example.englishmaster_be.Service.IExcelService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Tag(name = "Excel")
@RestController
@RequestMapping("/api/excel")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ImportExcelFileController {

    IExcelService excelService;

    @PostMapping(value = "/importExcel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @MessageResponse("File processed successfully")
    @SneakyThrows
    public CreateTopicByExcelFileResponse getCreateTopicByExcelFileDTO(@RequestParam("file") MultipartFile file) {

        return excelService.parseCreateTopicDTO(file);
    }

    @PostMapping(value = "/importQuestionPart67", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @MessageResponse("File processed successfully")
    @SneakyThrows
    public CreateListQuestionByExcelFileResponse getCreateQuestionPart67ByExcelFileDTO(
            @RequestParam("topicId") UUID topicId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("part") int part
    ) {

        return excelService.parseReadingPart67DTO(topicId, file, part);
    }

    @PostMapping(value = "/importQuestionPart5", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @MessageResponse("File processed successfully")
    @SneakyThrows
    public CreateListQuestionByExcelFileResponse getCreateQuestionByExcelFileDTO(
            @RequestParam("topicId") UUID topicId,
            @RequestParam("file") MultipartFile file
    ) {

        return excelService.parseReadingPart5DTO(topicId, file);
    }

    @PostMapping(value = "/importQuestionPart12", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @MessageResponse("File processed successfully")
    @SneakyThrows
    public CreateListQuestionByExcelFileResponse getCreateQuestionPart12ByExcelFileDTO(
            @RequestParam("topicId") UUID topicId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("part") int part
    ) {

        return excelService.parseListeningPart12DTO(topicId, file, part);
    }


    @PostMapping(value = "/importQuestionPart34", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @MessageResponse("File processed successfully")
    @SneakyThrows
    public CreateListQuestionByExcelFileResponse getCreateQuestionPart34ByExcelFileDTO(
            @RequestParam("topicId") UUID topicId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("part") int part
    ) {

        return excelService.parseListeningPart34DTO(topicId, file, part);
    }

    @PostMapping(value = "/importAllParts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @MessageResponse("File processed successfully")
    @SneakyThrows
    public CreateListQuestionByExcelFileResponse getCreateQuestionAllPartByExcelFileDTO(
            @RequestParam("topicId") UUID topicId,
            @RequestParam("file") MultipartFile file
    ) {

        return excelService.parseAllPartsDTO(topicId, file);
    }
}
