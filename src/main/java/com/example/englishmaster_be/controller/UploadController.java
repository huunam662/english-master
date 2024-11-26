package com.example.englishmaster_be.controller;

import com.example.englishmaster_be.common.response.ExceptionResponseModel;
import com.example.englishmaster_be.dto.DeleteRequestDto;
import com.example.englishmaster_be.model.response.DeleteResponse;
import com.example.englishmaster_be.common.response.ResponseModel;
import com.example.englishmaster_be.service.IUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/upload")
public class UploadController {

    @Autowired
    private IUploadService IUploadService;

    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseModel> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "dir", defaultValue = "/") String dir,
            @RequestParam(value = "isPrivateFile", defaultValue = "false") boolean isPrivateFile,
            @RequestParam(value = "topicId", required = false) UUID topicId,
            @RequestParam(value = "code", required = false) String code) {
        ResponseModel responseModel = new ResponseModel();
        String urlFile = IUploadService.upload(file, dir, isPrivateFile, topicId, code);
        if (urlFile != null && urlFile.startsWith("https")) {
            responseModel.setMessage("Successfully uploaded file");

            responseModel.setResponseData(urlFile);
            return ResponseEntity.ok(responseModel);
        } else {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Upload failed");
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @DeleteMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseModel> deleteFile(@RequestBody DeleteRequestDto dto) throws FileNotFoundException {
        ResponseModel responseModel = new ResponseModel();
        DeleteResponse response = IUploadService.delete(dto);
        if (response.getMessage().equalsIgnoreCase("Image deleted")) {
            responseModel.setMessage("Successfully uploaded file");

            responseModel.setResponseData(response.getMessage());
            return ResponseEntity.ok(responseModel);
        } else {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Deleted failed");
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setResponseData(null);
            return ResponseEntity.internalServerError().body(exceptionResponseModel);
        }
    }
}
