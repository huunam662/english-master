package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Helper.GetExtension;
import com.example.englishmaster_be.Model.Content;
import com.example.englishmaster_be.Model.User;
import com.example.englishmaster_be.Repository.ContentRepository;
import com.example.englishmaster_be.Service.IUploadService;
import com.example.englishmaster_be.Service.IUserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Service
public class UploadServiceImpl implements IUploadService {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private IUserService IUserService;

    private static String UPLOAD_API_URL = "https://meuupload.meu-solutions.com/api/v1.0/upload/single";
    private static String TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJidWNrZXQiOiJtZXVlbmdsaXNoIiwiaWF0IjoxNTE2MjM5MDIyfQ.jIAXLrrGhQseo8iZ6CNhXjf1Izs_nOK_RrcndzHYhJE18w0nPsx3PaHkG52zTARDWYasosOvTps_h_8-IAQ2FDZXgb2F5IhAukbDDEM7yL7bBtpZrgPnybbhu4TFcGMeealUvatifU1LUEKardPe62b12SdEiUkmag9lIjD81vBZ2KSAYJAFVchQ012dOPzT-O7qg2uBvfc-1XwKHBW5emMCEmlJh_DZ-rORSPvLpXzau9nhELAP9Hg-MCipmhhHR_4iaxX1ljhe4mq3T8rva86sLVDnOVzVB6I_Yl5S5zo2CCVM-5StxkAQABHtHzGeFsPYeHwK0ySwbYYHVF01lw";

    @Override
    public String upload(MultipartFile file, String dir, boolean isPrivateFile, UUID topicId, String code) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(TOKEN);  // Sử dụng Bearer token cho xác thực

        try {
            // Xây dựng phần tệp tin upload
            builder.part("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            }, MediaType.parseMediaType(Objects.requireNonNull(file.getContentType())));

            // Thêm các tham số khác
            builder.part("dir", dir);
            builder.part("isPrivateFile", isPrivateFile);

            HttpEntity<?> entity = new HttpEntity<>(builder.build(), headers);

            // Gửi request đến server upload
            ResponseEntity<String> response = restTemplate.exchange(
                    UPLOAD_API_URL, HttpMethod.POST, entity, String.class);

            // Parse JSON response to get URL
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response.getBody());

            // Kiểm tra "success" trong response
            boolean isSuccess = jsonResponse.path("success").asBoolean();
            if (isSuccess) {
                User currentUser = IUserService.currentUser();
                // Lấy giá trị của "url" trong "responseData"
                String url = jsonResponse.path("responseData").path("url").asText();
                Content content = new Content();
                content.setTopicId(topicId);
                content.setCode(code);
                content.setContentType(GetExtension.typeFile(file.getOriginalFilename()));
                content.setContentData(url);
                content.setUserUpdate(currentUser);
                content.setUserCreate(currentUser);

                contentRepository.save(content);
                return url;  // Trả về URL nếu upload thành công
            } else {
                // Nếu không thành công, lấy thông báo lỗi
                String errorMessage = jsonResponse.path("message").asText("Upload failed");
                return "Error: " + errorMessage;  // Trả về thông báo lỗi
            }
        } catch (IOException e) {
            throw new RuntimeException("Error uploading file: " + e.getMessage(), e);
        }
    }

}
