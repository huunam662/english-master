package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.DTO.DeleteRequestDto;
import com.example.englishmaster_be.Helper.GetExtension;
import com.example.englishmaster_be.Model.Content;
import com.example.englishmaster_be.Model.Response.DeleteResponse;
import com.example.englishmaster_be.Model.User;
import com.example.englishmaster_be.Repository.ContentRepository;
import com.example.englishmaster_be.Service.IUploadService;
import com.example.englishmaster_be.Service.IUserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class UploadServiceImpl implements IUploadService {
    private final RestTemplate restTemplate;
    private final ContentRepository contentRepository;
    private final IUserService userService;

    @Value("${upload.server.api.upload}")
    private String uploadApiUrl;

    @Value("${upload.server.token}")
    private String token;

    @Value("${upload.server.api.delete}")
    private String deleteApiUrl;

    public UploadServiceImpl(RestTemplate restTemplate, ContentRepository contentRepository, IUserService userService) {
        this.restTemplate = restTemplate;
        this.contentRepository = contentRepository;
        this.userService = userService;
    }

    private ResponseEntity<String> sendHttpRequest(String url, HttpMethod method, HttpHeaders headers, Object body) {
        HttpEntity<?> entity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(url, method, entity, String.class);
    }

    private HttpHeaders createHttpHeaders(MediaType contentType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(contentType);
        return headers;
    }

    @Override
    public String upload(MultipartFile file, String dir, boolean isPrivateFile, UUID topicId, String code) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is null or empty");
        }
        if (file.getContentType() == null) {
            throw new IllegalArgumentException("Invalid file type");
        }
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        HttpHeaders headers = createHttpHeaders(MediaType.MULTIPART_FORM_DATA);
        try {
            builder.part("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            }, MediaType.parseMediaType(Objects.requireNonNull(file.getContentType())));
            builder.part("dir", dir);
            builder.part("isPrivateFile", isPrivateFile);
            HttpEntity<?> entity = new HttpEntity<>(builder.build(), headers);
            ResponseEntity<String> response = restTemplate.exchange(uploadApiUrl, HttpMethod.POST, entity, String.class);
            if (response.getBody() == null) {
                throw new RuntimeException("Server response is empty");
            }
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response.getBody());
            boolean isSuccess = jsonResponse.path("success").asBoolean();
            if (isSuccess) {
                return handleSuccessfulUpload(jsonResponse, topicId, code, file);
            } else {
                String errorMessage = jsonResponse.path("message").asText("Upload failed");
                throw new RuntimeException("Error: " + errorMessage);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error uploading file: " + e.getMessage(), e);
        }
    }

    private String handleSuccessfulUpload(JsonNode jsonResponse, UUID topicId, String code, MultipartFile file) {
        User currentUser = userService.currentUser();
        String url = jsonResponse.path("responseData").path("url").asText();
        String existsContent = contentRepository.findContentDataByTopicIdAndCode(topicId, code);
        if (existsContent == null) {
            Content content = new Content();
            content.setTopicId(topicId);
            content.setCode(code);
            content.setContentType(GetExtension.typeFile(file.getOriginalFilename()));
            content.setContentData(url);
            content.setUserUpdate(currentUser);
            content.setUserCreate(currentUser);
            contentRepository.save(content);
            return url;
        } else {
            throw new RuntimeException("Code already exists in topic");
        }
    }

    @Transactional
    @Override
    public DeleteResponse delete(DeleteRequestDto dto) {
        String path = extractPathFromFilepath(dto.getFilepath());
        String encodedPath = Base64.getEncoder().encodeToString(path.getBytes(StandardCharsets.UTF_8));
        String url = deleteApiUrl + encodedPath;
        HttpHeaders headers = createHttpHeaders(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = sendHttpRequest(url, HttpMethod.DELETE, headers, null);
        if (response.getStatusCode() == HttpStatus.OK) {
            contentRepository.deleteByContentData(dto.getFilepath());
            return new DeleteResponse("Image deleted");
        } else {
            throw new RuntimeException("Failed to delete image: " + response.getBody());
        }
    }

    private String extractPathFromFilepath(String filepath) {
        return Optional.ofNullable(filepath)
                .filter(fp -> fp.contains("/public/"))
                .map(fp -> fp.substring(fp.indexOf("/public/")))
                .orElseThrow(() -> new IllegalArgumentException("The file path structure is not correct"));
    }
}