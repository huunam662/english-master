package com.example.englishmaster_be.domain.file_storage.service;

import com.example.englishmaster_be.domain.file_storage.dto.response.FileResponse;
import com.example.englishmaster_be.domain.file_storage.dto.response.ResourceResponse;
import com.google.cloud.storage.Blob;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface IFileStorageService {

    void init();

    ResourceResponse load(String fileName);

    FileResponse save(MultipartFile file);

    String nameFile(MultipartFile file);

    List<String> loadAll();

    boolean delete(String filename);

    boolean isExistingFile(String filename);

}
