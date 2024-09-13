package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Configuration.jwt.AuthEntryPointJwt;
import com.example.englishmaster_be.Model.ResponseModel;
import com.example.englishmaster_be.Service.IUploadService;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/upload")
public class UploadController {

    @Autowired
    private IUploadService IUploadService;


    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
            responseModel.setStatus("success");
            responseModel.setResponseData(urlFile);
            return ResponseEntity.ok(responseModel);
        } else {
            responseModel.setMessage("Upload failed");
            responseModel.setStatus("failed");
            responseModel.setResponseData(null);
            return ResponseEntity.badRequest().body(responseModel);
        }

    }

}
