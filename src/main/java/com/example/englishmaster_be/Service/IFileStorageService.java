package com.example.englishmaster_be.Service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface IFileStorageService {

    void init();

    Resource load(String filename);

    void save(MultipartFile file, String fileName);

    String nameFile(MultipartFile file);

    List<String> loadAll();

    boolean delete(String filename);
}
