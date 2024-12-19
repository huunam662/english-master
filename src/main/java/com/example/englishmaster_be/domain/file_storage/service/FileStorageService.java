package com.example.englishmaster_be.domain.file_storage.service;


import com.example.englishmaster_be.domain.file_storage.dto.response.FileResponse;
import com.example.englishmaster_be.domain.file_storage.dto.response.ResourceResponse;
import com.example.englishmaster_be.exception.template.ResourceNotFoundException;
import com.example.englishmaster_be.value.BucketValue;
import com.example.englishmaster_be.value.FileValue;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.firebase.cloud.StorageClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
public class FileStorageService implements IFileStorageService {

    FileValue fileValue;

    BucketValue bucketValue;

    @Override
    public void init() {
        Path root = Paths.get(fileValue.getFileSave());
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
//            Path file_storage = root.resolve(filename);
//            Resource resource = new UrlResource(file_storage.toUri());
//
//            if (resource.exists() || resource.isReadable()) {
//                return resource;
//            } else {
//                throw new RuntimeException("Could not read the file_storage!");
//            }
//        } catch (MalformedURLException e) {
//            throw new RuntimeException("ErrorEnum: " + e.getMessage());
//        }
//    }


    @Override
    public ResourceResponse load(String filename) {

        Bucket bucket = StorageClient.getInstance().bucket(bucketValue.getBucketNameStudentNodejs());

        Storage storage = bucket.getStorage();

        Blob blob = storage.get(bucketValue.getBucketNameStudentNodejs(), filename);

        if(blob == null) throw new ResourceNotFoundException("Image not found");

        byte[] inputStream = blob.getContent();

        String contentType = blob.getContentType() != null ? blob.getContentType() : "application/octet-stream";

        return ResourceResponse.builder()
                .fileName(blob.getName())
                .contentType(contentType)
                .contentLength(blob.getSize())
                .resource(new ByteArrayResource(inputStream))
                .build();
    }

//    @Override
//    public void save(MultipartFile file_storage, String fileName) {
//        Path root = Paths.get(fileSave);
//        try {
//            Files.copy(file_storage.getInputStream(), root.resolve(fileName));
//        } catch (exception e) {
//            if (e instanceof FileAlreadyExistsException) {
//                throw new RuntimeException("A file_storage of that name already exists.");
//            }
//            throw new RuntimeException(e.getMessage());
//        }
//
//    }

    @SneakyThrows
    @Override
    public FileResponse save(MultipartFile file) {
        try {

            String fileName = nameFile(file);

            Bucket bucket = StorageClient.getInstance().bucket(bucketValue.getBucketNameStudentNodejs());

            Blob blob = bucket.create(fileName, file.getBytes(), file.getContentType());

            return FileResponse.builder()
                    .fileName(fileValue.getPrefixLinkFileShow() + blob.getName())
                    .contentType(blob.getContentType())
                    .build();

        } catch (Exception e) {
            if (e instanceof FileAlreadyExistsException) {
                throw new FileAlreadyExistsException("A file_storage of that name already exists.");
            }
            throw new RuntimeException("Save file_storage failed");
        }
    }

    @Override
    public List<String> loadAll() {
        List<String> fileNames = new ArrayList<>();
        try {

            Bucket bucket = StorageClient.getInstance().bucket(bucketValue.getBucketNameStudentNodejs());

            bucket.list().iterateAll().forEach(blob -> {
                if (blob != null) fileNames.add(blob.getName());
            });

        } catch (Exception e) {
            throw new RuntimeException("Could not load the files!", e);
        }

        return fileNames;
    }


//    @Override
//    public boolean delete(String filename) {
//        Path root = Paths.get(fileSave);
//        try {
//            Path file_storage = root.resolve(filename);
//            return Files.deleteIfExists(file_storage);
//        } catch (IOException e) {
//            throw new RuntimeException("ErrorEnum: " + e.getMessage());
//        }
//    }


    @Override
    public boolean delete(String filename) {

        if(filename.contains(fileValue.getPrefixLinkFileShow()))
            filename = filename.replaceFirst(fileValue.getPrefixLinkFileShow(), "");

        Bucket bucket = StorageClient.getInstance().bucket(bucketValue.getBucketNameStudentNodejs());

        Blob blob = bucket.get(filename);

        if(blob == null) return false;

        return blob.delete();
    }

    @Override
    public boolean isExistingFile(String filename) {

        Bucket bucket = StorageClient.getInstance().bucket(bucketValue.getBucketNameStudentNodejs());

        Blob blob = bucket.get(filename);

        return blob != null;
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
