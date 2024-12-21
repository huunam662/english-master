package com.example.englishmaster_be.domain.upload.service;

import com.example.englishmaster_be.domain.file_storage.dto.response.FileResponse;
import com.example.englishmaster_be.util.GetExtensionUtil;
import com.example.englishmaster_be.domain.upload.dto.request.FileDeleteRequest;
import com.example.englishmaster_be.exception.template.CustomException;
import com.example.englishmaster_be.common.constant.error.ErrorEnum;
import com.example.englishmaster_be.exception.template.BadRequestException;
import com.example.englishmaster_be.value.UploadValue;
import com.example.englishmaster_be.model.content.ContentEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import com.example.englishmaster_be.model.content.ContentRepository;
import com.example.englishmaster_be.domain.user.service.IUserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UploadService implements IUploadService {

    RestTemplate restTemplate;

    ContentRepository contentRepository;

    UploadValue uploadValue;

    IUserService userService;


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

        if(topicId != null && code != null && !code.isEmpty())
            return handleSuccessfulUpload(fileResponse, topicId, code);

        return fileResponse;
    }

    @SneakyThrows
    @Override
    public FileResponse upload(MultipartFile file, String dir, boolean isPrivateFile) {

        if (file == null || file.isEmpty())
            throw new BadRequestException("File is null or empty");

        if (file.getContentType() == null)
            throw new BadRequestException("Invalid file_storage TypeEntity");

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
    protected FileResponse handleSuccessfulUpload(FileResponse fileResponse, UUID topicId, String code) {
        
        UserEntity currentUser = userService.currentUser();

        String existsContent = contentRepository.findContentDataByTopicIdAndCode(topicId, code);

        if(existsContent != null)
            throw new CustomException(ErrorEnum.CODE_EXISTED_IN_TOPIC);

        ContentEntity content = ContentEntity.builder()
                .topicId(topicId)
                .code(code)
                .contentType(fileResponse.getType())
                .contentData(fileResponse.getUrl())
                .userCreate(currentUser)
                .userUpdate(currentUser)
                .build();
        contentRepository.save(content);

        return fileResponse;
    }


    @Transactional
    @Override
    public void delete(FileDeleteRequest dto) {

        String path = extractPathFromFilepath(dto.getFilepath());
        String encodedPath = Base64.getEncoder().encodeToString(path.getBytes(StandardCharsets.UTF_8));
        String url = uploadValue.getDeleteApiUrl() + encodedPath;
        HttpHeaders headers = createHttpHeaders(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = sendHttpRequest(url, HttpMethod.DELETE, headers, null);

        if (response.getStatusCode() != HttpStatus.OK)
            throw new RuntimeException("Failed to delete image: " + response.getBody());

        contentRepository.deleteByContentData(dto.getFilepath());
    }

    private String extractPathFromFilepath(String filepath) {

        return Optional.ofNullable(filepath)
                .filter(fp -> fp.contains("/public/"))
                .map(fp -> fp.substring(fp.indexOf("/public/")))
                .orElseThrow(() -> new IllegalArgumentException("The file_storage path structure is not correct"));
    }
}