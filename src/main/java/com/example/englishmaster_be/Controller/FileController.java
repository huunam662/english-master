package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;
    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = fileStorageService.load(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @GetMapping("/showImage/{filename:.+}")
    public ResponseEntity<Resource> showImage(@PathVariable String filename) {
        Resource file = fileStorageService.load(filename);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG).body(file);
    }
}
