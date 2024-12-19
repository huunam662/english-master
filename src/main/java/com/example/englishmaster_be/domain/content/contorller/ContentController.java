package com.example.englishmaster_be.domain.content.contorller;


import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.domain.content.dto.request.ContentRequest;
import com.example.englishmaster_be.mapper.ContentMapper;
import com.example.englishmaster_be.model.content.ContentEntity;
import com.example.englishmaster_be.domain.content.dto.response.ContentResponse;
import com.example.englishmaster_be.domain.content.service.IContentService;
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
    @DefaultMessage("Get successfully")
    public ContentResponse getContentById(@RequestParam UUID id) {

        ContentEntity content = contentService.getContentByContentId(id);

        return ContentMapper.INSTANCE.toContentResponse(content);
    }

    @GetMapping("/contentData")
    @DefaultMessage("Get successfully")
    public ContentResponse getContentData(@RequestParam UUID topicId, @RequestParam String code) {

        ContentEntity content = contentService.getContentByTopicIdAndCode(topicId, code);

        return ContentMapper.INSTANCE.toContentResponse(content);
    }

    @DeleteMapping("/{contentId}")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Delete successfully")
    public void getContentData(@PathVariable UUID contentId) {

        contentService.deleteContent(contentId);
    }

    @GetMapping("/content")
    @DefaultMessage("Get successfully")
    public ContentResponse getContent(@RequestParam String contentData) {

        ContentEntity content = contentService.getContentByContentData(contentData);

        return ContentMapper.INSTANCE.toContentResponse(content);
    }

    @PostMapping(value = "/create-content")
    @DefaultMessage("Create successfully")
    public ContentResponse createContent(
            @RequestBody ContentRequest contentRequest
    ) {

        ContentEntity content = contentService.saveContent(contentRequest);

        return ContentMapper.INSTANCE.toContentResponse(content);
    }

    @PutMapping(value = "/{contentId}/update-content")
    @DefaultMessage("Update successfully")
    public ContentResponse updateContent(
            @PathVariable UUID contentId,
            @RequestBody ContentRequest contentRequest
    ) {

        contentRequest.setContentId(contentId);

        ContentEntity content = contentService.saveContent(contentRequest);

        return ContentMapper.INSTANCE.toContentResponse(content);
    }


}
