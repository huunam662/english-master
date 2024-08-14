package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.DTO.Topic.CreateListQuestionByExcelFileDTO;
import com.example.englishmaster_be.DTO.Topic.CreateTopicByExcelFileDTO;
import com.example.englishmaster_be.Model.ResponseModel;
import com.example.englishmaster_be.Service.IExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/excel")
public class ImportExcelFileController {
    @Autowired
    private IExcelService excelService;

    @PostMapping(value = "/importExcel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseModel getCreateTopicByExcelFileDTO(@RequestParam("file") MultipartFile file) throws IOException {
        ResponseModel responseModel = new ResponseModel();
        if (file.isEmpty()) {
            responseModel.setStatus("error");
            responseModel.setMessage("Please select a file to upload");
            return responseModel;
        }
        try {
            CreateTopicByExcelFileDTO createTopicByExcelFileDTO = excelService.parseCreateTopicDTO(file);
            responseModel.setResponseData(createTopicByExcelFileDTO);
            responseModel.setStatus("success");
            responseModel.setMessage("File processed successfully");
        } catch (Exception e) {
            responseModel.setStatus("error");
            responseModel.setMessage("Error processing file: " + e.getMessage());
        }
        return responseModel;
    }

    @PostMapping(value = "/importQuestionPart5", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseModel getCreateQuestionByExcelFileDTO(@RequestParam("file") MultipartFile file) throws IOException {
        ResponseModel responseModel = new ResponseModel();
        if (file.isEmpty()) {
            responseModel.setStatus("error");
            responseModel.setMessage("Please select a file to upload");
            return responseModel;
        }
        try {
            CreateListQuestionByExcelFileDTO createTopicByExcelFileDTO = excelService.parseReadingPart5DTO(file);
            responseModel.setResponseData(createTopicByExcelFileDTO);
            responseModel.setStatus("success");
            responseModel.setMessage("File processed successfully");
        } catch (Exception e) {
            responseModel.setStatus("error");
            responseModel.setMessage("Error processing file: " + e.getMessage());
        }
        return responseModel;
    }

    @PostMapping(value = "/importQuestionPart67", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseModel getCreateQuestionPart67ByExcelFileDTO(@RequestParam("file") MultipartFile file, @RequestParam("part") int part) throws IOException {
        ResponseModel responseModel = new ResponseModel();
        if (file.isEmpty()) {
            responseModel.setStatus("error");
            responseModel.setMessage("Please select a file to upload");
            return responseModel;
        }
        try {
            CreateListQuestionByExcelFileDTO createTopicByExcelFileDTO = excelService.parseReadingPart67DTO(file, part);
            responseModel.setResponseData(createTopicByExcelFileDTO);
            responseModel.setStatus("success");
            responseModel.setMessage("File processed successfully");
        } catch (Exception e) {
            responseModel.setStatus("error");
            responseModel.setMessage("Error processing file: " + e.getMessage());
        }
        return responseModel;
    }

    @PostMapping(value = "/importQuestionPart12", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseModel getCreateQuestionPart12ByExcelFileDTO(@RequestParam("file") MultipartFile file, @RequestParam("part") int part) throws IOException {
        ResponseModel responseModel = new ResponseModel();
        if (file.isEmpty()) {
            responseModel.setStatus("error");
            responseModel.setMessage("Please select a file to upload");
            return responseModel;
        }
        try {
            CreateListQuestionByExcelFileDTO createTopicByExcelFileDTO = excelService.parseListeningPart12DTO(file, part);
            responseModel.setResponseData(createTopicByExcelFileDTO);
            responseModel.setStatus("success");
            responseModel.setMessage("File processed successfully");
        } catch (Exception e) {
            responseModel.setStatus("error");
            responseModel.setMessage("Error processing file: " + e.getMessage());
        }
        return responseModel;
    }
}
