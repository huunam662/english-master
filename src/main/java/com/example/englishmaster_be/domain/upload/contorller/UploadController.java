package com.example.englishmaster_be.domain.upload.contorller;


import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.domain.upload.dto.request.FileDeleteRequest;
import com.example.englishmaster_be.domain.upload.service.IUploadService;
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
    @DefaultMessage("Successfully uploaded file_storage")
    public String uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "dir", defaultValue = "/") String dir,
            @RequestParam(value = "isPrivateFile", defaultValue = "false") boolean isPrivateFile,
            @RequestParam(value = "topicId", required = false) UUID topicId,
            @RequestParam(value = "code", required = false) String code
    ) {

        return uploadService.upload(file, dir, isPrivateFile, topicId, code);
    }

    @DeleteMapping
    @DefaultMessage("Successfully delete file_storage")
    @SneakyThrows
    public void deleteFile(@RequestBody FileDeleteRequest dto) {

        uploadService.delete(dto);
    }
}
