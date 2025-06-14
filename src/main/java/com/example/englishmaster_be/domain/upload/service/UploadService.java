package com.example.englishmaster_be.domain.upload.service;

import com.example.englishmaster_be.shared.dto.response.FileResponse;
import com.example.englishmaster_be.domain.upload.dto.request.FileDeleteRequest;
import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.common.constant.error.Error;
import com.example.englishmaster_be.value.UploadValue;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UploadService implements IUploadService {

    RestTemplate restTemplate;

    UploadValue uploadValue;


    private ResponseEntity<String> sendHttpRequest(String url, HttpMethod method, HttpHeaders headers, Object body) {
        HttpEntity<?> entity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(url, method, entity, String.class);
    }

    private HttpHeaders createHttpHeaders(MediaType contentType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(uploadValue.getToken());
        headers.setContentType(contentType);
        return headers;
    }

    @Transactional
    @Override
    public FileResponse upload(MultipartFile file, String dir, boolean isPrivateFile, UUID topicId, String code) {

        FileResponse fileResponse = upload(file, dir, isPrivateFile);

        return fileResponse;
    }

    @SneakyThrows
    @Override
    public FileResponse upload(MultipartFile file, String dir, boolean isPrivateFile) {

        if (file == null || file.isEmpty())
            throw new ErrorHolder(Error.BAD_REQUEST, "File is null or empty");

        if (file.getContentType() == null)
            throw new ErrorHolder(Error.BAD_REQUEST , "Invalid file storage type");

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        HttpHeaders headers = createHttpHeaders(MediaType.MULTIPART_FORM_DATA);

        builder.part("file", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        }, MediaType.parseMediaType(Objects.requireNonNull(file.getContentType())));
        builder.part("dir", dir);
        builder.part("isPrivateFile", isPrivateFile);

        HttpEntity<?> entity = new HttpEntity<>(builder.build(), headers);
        ResponseEntity<String> response = restTemplate.exchange(uploadValue.getUploadApiUrl(), HttpMethod.POST, entity, String.class);

        if (response.getBody() == null)
            throw new RuntimeException("Server response is empty");

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = objectMapper.readTree(response.getBody());
        boolean isSuccess = jsonResponse.path("success").asBoolean();

        if(!isSuccess){
            String errorMessage = jsonResponse.path("message").asText("Upload failed");
            throw new RuntimeException("ErrorEnum: " + errorMessage);
        }

        JsonNode responseData = jsonResponse.path("responseData");
        String url = responseData.path("url").asText();

        if(url == null || !url.startsWith("https"))
            throw new RuntimeException("Upload failed");

        String typeFile = responseData.path("mimetype").asText(null);

        return FileResponse.builder()
                .url(url)
                .type(typeFile)
                .build();
    }

    @Override
    public FileResponse upload(MultipartFile file) {

        String dir = "/";

        boolean isPrivateFile = false;

        return upload(file, dir, isPrivateFile);
    }


    @Transactional
    @Override
    public void delete(FileDeleteRequest dto) {

        String path = extractPathFromFilepath(dto.getFilepath());

        if(path == null) return;

        String encodedPath = Base64.getEncoder().encodeToString(path.getBytes(StandardCharsets.UTF_8));
        String url = uploadValue.getDeleteApiUrl() + encodedPath;
        HttpHeaders headers = createHttpHeaders(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = sendHttpRequest(url, HttpMethod.DELETE, headers, null);

        if (response.getStatusCode() != HttpStatus.OK)
            throw new RuntimeException("Failed to delete image: " + response.getBody());
    }

    @Transactional
    @Override
    public void delete(String filepath) {

        String path = extractPathFromFilepath(filepath);

        if(path == null) return;

        String encodedPath = Base64.getEncoder().encodeToString(path.getBytes(StandardCharsets.UTF_8));
        String url = uploadValue.getDeleteApiUrl() + encodedPath;
        HttpHeaders headers = createHttpHeaders(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = sendHttpRequest(url, HttpMethod.DELETE, headers, null);

        if (response.getStatusCode() != HttpStatus.OK)
            throw new RuntimeException("Failed to delete image: " + response.getBody());
    }

    private String extractPathFromFilepath(String filepath) {

        return Optional.ofNullable(filepath)
                .filter(fp -> fp.contains("/public/"))
                .map(fp -> fp.substring(fp.indexOf("/public/")))
                .orElse(null);
    }
}