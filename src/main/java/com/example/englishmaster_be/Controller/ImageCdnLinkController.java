package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Model.Response.ResponseModel;
import com.example.englishmaster_be.Repository.ContentRepository;
import com.example.englishmaster_be.Repository.TopicRepository;
import com.example.englishmaster_be.Service.IContentService;
import com.example.englishmaster_be.Service.ITopicService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "Image CDN Link")
@RestController
@RequestMapping("api/cdn")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ImageCdnLinkController {

    IContentService contentService;

    ITopicService topicService;

    @GetMapping
    public List<String> getImageCdnLink() {

        return contentService.getImageCdnLink();
    }

    @GetMapping("topic")
    public List<String> getImageCdnLinkTopic() {

        return topicService.getImageCdnLinkTopic();
    }
}
