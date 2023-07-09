package com.example.englishmaster_be.Service;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;


public interface IFileStorageService {

    void init();
    Resource load(String filename);
    void save(MultipartFile file, String fileName);

    String nameFile(MultipartFile file);

    Stream<Path> loadAll();

    boolean delete(String filename);
}
