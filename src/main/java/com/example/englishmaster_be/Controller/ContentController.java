package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Configuration.global.annotation.MessageResponse;
import com.example.englishmaster_be.DTO.Content.UpdateContentDTO;
import com.example.englishmaster_be.Model.Content;
import com.example.englishmaster_be.Model.Response.ContentResponse;
import com.example.englishmaster_be.Service.IContentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/content")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContentController {

    IContentService contentService;

    @GetMapping
    @MessageResponse("Get Content successfully")
    public ContentResponse getContentById(@RequestParam UUID id) {

        Content content = contentService.getContentToContentId(id);

        return new ContentResponse(content);
    }

    @GetMapping("/contentData")
    @MessageResponse("Get Content successfully")
    public ContentResponse getContentData(@RequestParam UUID topicId, @RequestParam String code) {

        Content content = contentService.getContentByTopicIdAndCode(topicId, code);

        return new ContentResponse(content);
    }

    @GetMapping("/content")
    @MessageResponse("Get Content successfully")
    public ContentResponse getContent(@RequestParam String contentData) {

        Content content = contentService.getContentByContentData(contentData);;

        return new ContentResponse(content);
    }

    @PutMapping("/update-content")
    @MessageResponse("Get Content successfully")
    public ContentResponse updateContent(
            @RequestBody UpdateContentDTO updateContentDTO,
            @RequestParam MultipartFile file
    ) {

        updateContentDTO.setFile(file);

        Content content = contentService.saveContent(updateContentDTO);

        return new ContentResponse(content);
    }
}
