package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Configuration.global.annotation.MessageResponse;
import com.example.englishmaster_be.DTO.Content.SaveContentDTO;
import com.example.englishmaster_be.DTO.Content.UpdateContentDTO;
import com.example.englishmaster_be.Model.Content;
import com.example.englishmaster_be.Model.Response.ContentResponse;
import com.example.englishmaster_be.Service.IContentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Content")
@RestController
@RequestMapping("/api/content")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContentController {

    IContentService contentService;


    @GetMapping
    @MessageResponse("Get content successfully")
    public ContentResponse getContentById(@RequestParam UUID id) {

        Content content = contentService.getContentToContentId(id);

        return new ContentResponse(content);
    }

    @GetMapping("/contentData")
    @MessageResponse("Get content successfully")
    public ContentResponse getContentData(@RequestParam UUID topicId, @RequestParam String code) {

        Content content = contentService.getContentByTopicIdAndCode(topicId, code);

        return new ContentResponse(content);
    }

    @GetMapping("/content")
    @MessageResponse("Get content successfully")
    public ContentResponse getContent(@RequestParam String contentData) {

        Content content = contentService.getContentByContentData(contentData);

        return new ContentResponse(content);
    }

    @PostMapping(value = "/create-content", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @MessageResponse("Create content successfully")
    public ContentResponse createContent(
            @ModelAttribute SaveContentDTO createContentDTO
    ) {

        Content content = contentService.saveContent(createContentDTO);

        return new ContentResponse(content);
    }

    @PutMapping(value = "/update-content", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @MessageResponse("Update content successfully")
    public ContentResponse updateContent(
            @ModelAttribute UpdateContentDTO updateContentDTO
    ) {

        Content content = contentService.saveContent(updateContentDTO);

        return new ContentResponse(content);
    }


}
