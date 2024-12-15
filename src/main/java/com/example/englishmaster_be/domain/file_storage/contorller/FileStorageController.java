package com.example.englishmaster_be.domain.file_storage.contorller;


import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.domain.file_storage.dto.response.FileResponse;
import com.example.englishmaster_be.domain.file_storage.service.IFileStorageService;
import com.google.cloud.storage.Blob;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "File")
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileStorageController {

    IFileStorageService fileStorageService;

    @GetMapping("/{filename:.+}")
    @DefaultMessage("Load file_storage successfully")
    public Resource getFile(
            @Parameter(hidden = true) HttpServletResponse response,
            @PathVariable String filename
    ) {

        Resource file = fileStorageService.load(filename);

        response.setHeader(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\""
        );

        return file;
    }

    @GetMapping("/showImage/{filename:.+}")
    @DefaultMessage("Load file_storage successfully")
    public Resource showImage(
            @Parameter(hidden = true) HttpServletResponse response,
            @PathVariable String filename
    ) {

        Resource file = fileStorageService.load(filename);

        response.setContentType(MediaType.IMAGE_JPEG_VALUE);

        return file;
    }


    @GetMapping("/showAudio/{filename:.+}")
    @DefaultMessage("Load file_storage successfully")
    public Resource showAudio(
            @Parameter(hidden = true) HttpServletResponse response,
            @PathVariable String filename
    ) {

        Resource file = fileStorageService.load(filename);

        response.setContentType(MediaType.valueOf("audio/mpeg").toString());

        return file;
    }

    @GetMapping("/getImageName")
    @DefaultMessage("Load file_storage names successfully")
    public List<String> showImages() {

        return fileStorageService.loadAll();
    }

    @PostMapping(value = "/saveImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @DefaultMessage("Save file_storage successfully")
    public FileResponse saveImage(@RequestParam("file") MultipartFile file) {

        Blob blob = fileStorageService.save(file);

        return FileResponse.builder()
                .fileName(blob.getName())
                .build();
    }

    @DeleteMapping("/deleteImage/{fileName}")
    @DefaultMessage("Delete file_storage successfully")
    public void deleteImage(@PathVariable("fileName") String fileName) {

        fileStorageService.delete(fileName);
    }
}
