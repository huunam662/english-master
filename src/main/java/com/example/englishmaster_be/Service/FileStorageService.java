package com.example.englishmaster_be.Service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface FileStorageService {

    void init();
    Resource load(String filename);
    String save(MultipartFile file);

    Stream<Path> loadAll();
}
