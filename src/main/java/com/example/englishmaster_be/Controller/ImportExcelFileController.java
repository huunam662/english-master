package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Model.Response.ExceptionResponseModel;
import com.example.englishmaster_be.DTO.Topic.CreateListQuestionByExcelFileDTO;
import com.example.englishmaster_be.DTO.Topic.CreateTopicByExcelFileDTO;
import com.example.englishmaster_be.Model.Response.ResponseModel;
import com.example.englishmaster_be.Service.IExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/excel")
public class ImportExcelFileController {
    @Autowired
    private IExcelService excelService;

    @PostMapping(value = "/importExcel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseModel getCreateTopicByExcelFileDTO(@RequestParam("file") MultipartFile file) {
        ResponseModel responseModel = new ResponseModel();
        ExceptionResponseModel errorResponseModel = new ExceptionResponseModel();
        if (file.isEmpty()) {
            errorResponseModel.setMessage("Please select a file to upload");
            errorResponseModel.setStatus(HttpStatus.BAD_REQUEST);
            return errorResponseModel;
        }
        try {
            CreateTopicByExcelFileDTO createTopicByExcelFileDTO = excelService.parseCreateTopicDTO(file);
            responseModel.setResponseData(createTopicByExcelFileDTO);

            responseModel.setMessage("File processed successfully");
        } catch (Exception e) {
            errorResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            errorResponseModel.setMessage("Error proces sing file: " + e.getMessage());
            return errorResponseModel;
        }
        return responseModel;
    }

    @PostMapping(value = "/importQuestionPart67", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseModel getCreateQuestionPart67ByExcelFileDTO(@RequestParam("topicId") UUID topicId, @RequestParam("file") MultipartFile file, @RequestParam("part") int part) throws IOException {
        ResponseModel responseModel = new ResponseModel();
        ExceptionResponseModel errorResponseModel = new ExceptionResponseModel();
        if (file.isEmpty()) {
            errorResponseModel.setStatus(HttpStatus.BAD_REQUEST);
            errorResponseModel.setMessage("Please select a file to upload");
            return errorResponseModel;
        }

        // Validate that Part is either 6 or 7
        if (part != 6 && part != 7) {
            errorResponseModel.setStatus(HttpStatus.BAD_REQUEST);
            errorResponseModel.setMessage("Invalid Part Value. It must be either 6 or 7.");
            return errorResponseModel;
        }

        try {
            CreateListQuestionByExcelFileDTO createTopicByExcelFileDTO = excelService.parseReadingPart67DTO(topicId, file, part);
            responseModel.setResponseData(createTopicByExcelFileDTO);

            responseModel.setMessage("File processed successfully");
        } catch (Exception e) {
            errorResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            errorResponseModel.setMessage("Error processing file: " + e.getMessage());
            return errorResponseModel;
        }
        return responseModel;
    }

    @PostMapping(value = "/importQuestionPart5", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseModel getCreateQuestionByExcelFileDTO(@RequestParam("topicId") UUID topicId, @RequestParam("file") MultipartFile file) throws IOException {
        ResponseModel responseModel = new ResponseModel();
        ExceptionResponseModel errorResponseModel = new ExceptionResponseModel();
        if (file.isEmpty()) {
            errorResponseModel.setStatus(HttpStatus.BAD_REQUEST);
            errorResponseModel.setMessage("Please select a file to upload");
            return errorResponseModel;
        }
        try {
            CreateListQuestionByExcelFileDTO createTopicByExcelFileDTO = excelService.parseReadingPart5DTO(topicId, file);
            responseModel.setResponseData(createTopicByExcelFileDTO);

            responseModel.setMessage("File processed successfully");
        } catch (Exception e) {
            errorResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            errorResponseModel.setMessage("Error processing file: " + e.getMessage());
            return errorResponseModel;
        }
        return responseModel;
    }

    @PostMapping(value = "/importQuestionPart12", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseModel getCreateQuestionPart12ByExcelFileDTO(@RequestParam("topicId") UUID topicId, @RequestParam("file") MultipartFile file, @RequestParam("part") int part) throws IOException {
        ResponseModel responseModel = new ResponseModel();
        ExceptionResponseModel errorResponseModel = new ExceptionResponseModel();
        if (file.isEmpty()) {
            errorResponseModel.setStatus(HttpStatus.BAD_REQUEST);
            errorResponseModel.setMessage("Please select a file to upload");
            return errorResponseModel;
        }

        // Validate that Part is either 1 or 2
        if (part != 1 && part != 2) {
            errorResponseModel.setStatus(HttpStatus.BAD_REQUEST);
            errorResponseModel.setMessage("Invalid Part Value. It must be either 1 or 2.");
            return errorResponseModel;
        }

        try {
            CreateListQuestionByExcelFileDTO createTopicByExcelFileDTO = excelService.parseListeningPart12DTO(topicId, file, part);
            responseModel.setResponseData(createTopicByExcelFileDTO);

            responseModel.setMessage("File processed successfully");
        } catch (Exception e) {
            errorResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            errorResponseModel.setMessage("Error processing file: " + e.getMessage());
            return errorResponseModel;
        }
        return responseModel;
    }


    @PostMapping(value = "/importQuestionPart34", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseModel getCreateQuestionPart34ByExcelFileDTO(@RequestParam("topicId") UUID topicId, @RequestParam("file") MultipartFile file, @RequestParam("part") int part) throws IOException {
        ResponseModel responseModel = new ResponseModel();
        ExceptionResponseModel errorResponseModel = new ExceptionResponseModel();
        if (file.isEmpty()) {
            errorResponseModel.setStatus(HttpStatus.BAD_REQUEST);
            errorResponseModel.setMessage("Please select a file to upload");
            return errorResponseModel;
        }

        // Validate that Part is either 3 or 4
        if (part != 3 && part != 4) {
            errorResponseModel.setStatus(HttpStatus.BAD_REQUEST);
            errorResponseModel.setMessage("Invalid Part Value. It must be either 3 or 4.");
            return errorResponseModel;
        }

        try {
            CreateListQuestionByExcelFileDTO createTopicByExcelFileDTO = excelService.parseListeningPart34DTO(topicId, file, part);
            responseModel.setResponseData(createTopicByExcelFileDTO);

            responseModel.setMessage("File processed successfully");
        } catch (Exception e) {
            errorResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            errorResponseModel.setMessage("Error processing file: " + e.getMessage());
            return errorResponseModel;
        }
        return responseModel;
    }

    @PostMapping(value = "/importAllParts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseModel getCreateQuestionAllPartByExcelFileDTO(@RequestParam("topicId") UUID topicId, @RequestParam("file") MultipartFile file) throws IOException {
        ResponseModel responseModel = new ResponseModel();
        ExceptionResponseModel errorResponseModel = new ExceptionResponseModel();
        if (file.isEmpty()) {
            errorResponseModel.setStatus(HttpStatus.BAD_REQUEST);
            errorResponseModel.setMessage("Please select a file to upload");
            return errorResponseModel;
        }
        try {
            CreateListQuestionByExcelFileDTO createTopicByExcelFileDTO = excelService.parseAllPartsDTO(topicId, file);
            responseModel.setResponseData(createTopicByExcelFileDTO);

            responseModel.setMessage("File processed successfully");
        } catch (Exception e) {
            errorResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);

            errorResponseModel.setMessage("Error processing file: " + e.getMessage());
            return errorResponseModel;
        }
        return responseModel;
    }
}
