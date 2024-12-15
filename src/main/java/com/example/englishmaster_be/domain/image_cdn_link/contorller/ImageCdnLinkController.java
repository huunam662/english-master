package com.example.englishmaster_be.domain.image_cdn_link.contorller;

import com.example.englishmaster_be.domain.content.service.IContentService;
import com.example.englishmaster_be.domain.topic.service.ITopicService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Image CDN Link")
@RestController
@RequestMapping("/cdn")
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
