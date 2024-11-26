package com.example.englishmaster_be.service.impl;


import com.example.englishmaster_be.service.IFileStorageService;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.firebase.cloud.StorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileStorageServiceImpl implements IFileStorageService {

    @Value("${masterE.fileSave}")
    private String fileSave;
//    private final Path root = Paths.get("D:\\Workplace\\FileEnglishMaster");
//    private final Path root = Paths.get(fileSave);


    @Override
    public void init() {
        Path root = Paths.get(fileSave);
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

//    @Override
//    public Resource load(String filename) {
//        Path root = Paths.get(fileSave);
//        try {
//            Path file = root.resolve(filename);
//            Resource resource = new UrlResource(file.toUri());
//
//            if (resource.exists() || resource.isReadable()) {
//                return resource;
//            } else {
//                throw new RuntimeException("Could not read the file!");
//            }
//        } catch (MalformedURLException e) {
//            throw new RuntimeException("Error: " + e.getMessage());
//        }
//    }


    @Override
    public Resource load(String filename) {
        try {
            String bucketName = "connect-student-nodejs.appspot.com";
            Bucket bucket = StorageClient.getInstance().bucket(bucketName);
            Storage storage = bucket.getStorage();
            Blob blob = storage.get(bucketName, filename);

            if (blob != null) {
                byte[] content = blob.getContent();
                return new ByteArrayResource(content);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

//    @Override
//    public void save(MultipartFile file, String fileName) {
//        Path root = Paths.get(fileSave);
//        try {
//            Files.copy(file.getInputStream(), root.resolve(fileName));
//        } catch (Exception e) {
//            if (e instanceof FileAlreadyExistsException) {
//                throw new RuntimeException("A file of that name already exists.");
//            }
//            throw new RuntimeException(e.getMessage());
//        }
//
//    }

    @Override
    public void save(MultipartFile file, String fileName) {
        try {
            String bucketName = "connect-student-nodejs.appspot.com";
            Bucket bucket = StorageClient.getInstance().bucket(bucketName);

            bucket.create(fileName, file.getBytes(), file.getContentType());
        } catch (Exception e) {
            if (e instanceof FileAlreadyExistsException) {
                throw new RuntimeException("A file of that name already exists.");
            }
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<String> loadAll() {
        List<String> fileNames = new ArrayList<>();
        try {
            String bucketName = "connect-student-nodejs.appspot.com";
            Bucket bucket = StorageClient.getInstance().bucket(bucketName);

            for (Blob blob : bucket.list().iterateAll()) {
                if (blob != null) {
                    fileNames.add(blob.getName());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not load the files!", e);
        }
        return fileNames;
    }


//    @Override
//    public boolean delete(String filename) {
//        Path root = Paths.get(fileSave);
//        try {
//            Path file = root.resolve(filename);
//            return Files.deleteIfExists(file);
//        } catch (IOException e) {
//            throw new RuntimeException("Error: " + e.getMessage());
//        }
//    }

    @Override
    public boolean delete(String filename) {
        String bucketName = "connect-student-nodejs.appspot.com";
        Bucket bucket = StorageClient.getInstance().bucket(bucketName);
        return bucket.get(filename).delete();
    }

    @Override
    public String nameFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);
        String fileNameDelete = deleteExtension(originalFilename);

        LocalDateTime currentTime = LocalDateTime.now();

        // Định dạng thời gian hiện tại thành chuỗi
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = currentTime.format(formatter);

        // Tạo tên tệp tin mới bằng cách kết hợp tên gốc và thời gian hiện tại

        return fileNameDelete + "_" + timestamp + extension;
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex > -1 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex);
        }
        return "";
    }

    private String deleteExtension(String filename) {
        int dotIndex = filename.lastIndexOf(".");
        return filename.substring(0, dotIndex);
    }
}
