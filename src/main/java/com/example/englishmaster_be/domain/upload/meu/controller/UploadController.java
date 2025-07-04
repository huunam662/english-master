package com.example.englishmaster_be.domain.upload.meu.controller;


import com.example.englishmaster_be.domain.upload.meu.request.FileDeleteRequest;
import com.example.englishmaster_be.domain.upload.meu.service.IUploadService;
import com.example.englishmaster_be.common.dto.response.FileResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Tag(name = "Upload")
@RestController
@RequestMapping("/v1/upload")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UploadController {

    IUploadService uploadService;

    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileResponse uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "dir", defaultValue = "/") String dir,
            @RequestParam(value = "isPrivateFile", defaultValue = "false") boolean isPrivateFile,
            @Schema(description = "Id of topic -> type UUID, required is false")
            @RequestParam(value = "topicId", required = false) UUID topicId,
            @Schema(description = "Code -> type String, required is false")
            @RequestParam(value = "code", required = false) String code
    ) {

        return uploadService.upload(file, dir, isPrivateFile, topicId, code);
    }

    @DeleteMapping
    @SneakyThrows
    public void deleteFile(@RequestBody FileDeleteRequest dto) {

        uploadService.delete(dto);
    }
}
