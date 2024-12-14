package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Configuration.global.annotation.MessageResponse;
import com.example.englishmaster_be.Model.Request.Content.ContentRequest;
import com.example.englishmaster_be.Mapper.ContentMapper;
import com.example.englishmaster_be.entity.ContentEntity;
import com.example.englishmaster_be.Model.Response.ContentResponse;
import com.example.englishmaster_be.Service.IContentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Content")
@RestController
@RequestMapping("/content")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContentController {

    IContentService contentService;


    @GetMapping
    @MessageResponse("Get content successfully")
    public ContentResponse getContentById(@RequestParam UUID id) {

        ContentEntity content = contentService.getContentByContentId(id);

        return ContentMapper.INSTANCE.toContentResponse(content);
    }

    @GetMapping("/contentData")
    @MessageResponse("Get content successfully")
    public ContentResponse getContentData(@RequestParam UUID topicId, @RequestParam String code) {

        ContentEntity content = contentService.getContentByTopicIdAndCode(topicId, code);

        return ContentMapper.INSTANCE.toContentResponse(content);
    }

    @DeleteMapping("/{contentId}")
    @PreAuthorize("hasRole('ADMIN')")
    @MessageResponse("Delete content successfully")
    public void getContentData(@PathVariable UUID contentId) {

        contentService.deleteContent(contentId);
    }

    @GetMapping("/content")
    @MessageResponse("Get content successfully")
    public ContentResponse getContent(@RequestParam String contentData) {

        ContentEntity content = contentService.getContentByContentData(contentData);

        return ContentMapper.INSTANCE.toContentResponse(content);
    }

    @PostMapping(value = "/create-content", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @MessageResponse("Create content successfully")
    public ContentResponse createContent(
            @ModelAttribute ContentRequest contentRequest
    ) {

        ContentEntity content = contentService.saveContent(contentRequest);

        return ContentMapper.INSTANCE.toContentResponse(content);
    }

    @PutMapping(value = "/{contentId}/update-content", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @MessageResponse("Update content successfully")
    public ContentResponse updateContent(
            @PathVariable UUID contentId,
            @ModelAttribute ContentRequest contentRequest
    ) {

        contentRequest.setContentId(contentId);

        ContentEntity content = contentService.saveContent(contentRequest);

        return ContentMapper.INSTANCE.toContentResponse(content);
    }


}
