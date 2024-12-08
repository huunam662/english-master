package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.DTO.DeleteRequestDTO;
import com.example.englishmaster_be.Exception.CustomException;
import com.example.englishmaster_be.Exception.Error;
import com.example.englishmaster_be.Exception.Response.BadRequestException;
import com.example.englishmaster_be.Helper.GetExtension;
import com.example.englishmaster_be.Model.Content;
import com.example.englishmaster_be.Model.Response.DeleteResponse;
import com.example.englishmaster_be.Model.User;
import com.example.englishmaster_be.Repository.ContentRepository;
import com.example.englishmaster_be.Service.IUploadService;
import com.example.englishmaster_be.Service.IUserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
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
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UploadServiceImpl implements IUploadService {

    @Value("${upload.server.api.upload}")
    static String uploadApiUrl;

    @Value("${upload.server.token}")
    static String token;

    @Value("${upload.server.api.delete}")
    static String deleteApiUrl;

    RestTemplate restTemplate;

    ContentRepository contentRepository;

    IUserService userService;


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


    @SneakyThrows
    @Override
    public String upload(MultipartFile file, String dir, boolean isPrivateFile, UUID topicId, String code) {

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is null or empty");
        }
        if (file.getContentType() == null) {
            throw new BadRequestException("Invalid file Type");
        }

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
        ResponseEntity<String> response = restTemplate.exchange(uploadApiUrl, HttpMethod.POST, entity, String.class);
        if (response.getBody() == null) {
            throw new RuntimeException("Server response is empty");
        }
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = objectMapper.readTree(response.getBody());
        boolean isSuccess = jsonResponse.path("success").asBoolean();
        if (isSuccess) {
            String url = jsonResponse.path("responseData").path("url").asText();
            if (topicId != null && code != null && !code.isEmpty()) {
                return handleSuccessfulUpload(jsonResponse, topicId, code, file);
            }

            if(url == null || !url.startsWith("https"))
                throw new RuntimeException("Upload failed");

            return url;
        } else {
            String errorMessage = jsonResponse.path("message").asText("Upload failed");
            throw new RuntimeException("Error: " + errorMessage);
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
            throw new CustomException(Error.CODE_EXISTED_IN_TOPIC);
        }
    }


    @Transactional
    @Override
    public DeleteResponse delete(DeleteRequestDTO dto) {
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