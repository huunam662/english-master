package com.example.englishmaster_be.domain.topic_type.controller;

import com.example.englishmaster_be.domain.topic_type.dto.response.TopicTypeResponse;
import com.example.englishmaster_be.domain.topic_type.service.ITopicTypeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Topic Type")
@RestController
@RequestMapping(name = "/topic-type")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TopicTypeController implements ITopicTypeController{

    ITopicTypeService topicTypeService;

    @Override
    public TopicTypeResponse getSingleTopicType(UUID topicTypeId) {
        return null;
    }
}
