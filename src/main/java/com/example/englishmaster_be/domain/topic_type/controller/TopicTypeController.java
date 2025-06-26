package com.example.englishmaster_be.domain.topic_type.controller;

import com.example.englishmaster_be.domain.topic_type.dto.request.CreateTopicTypeRequest;
import com.example.englishmaster_be.domain.topic_type.dto.request.UpdateTopicTypeRequest;
import com.example.englishmaster_be.domain.topic_type.dto.response.TopicTypeKeyResponse;
import com.example.englishmaster_be.domain.topic_type.dto.response.TopicTypeResponse;
import com.example.englishmaster_be.domain.topic_type.mapper.TopicTypeMapper;
import com.example.englishmaster_be.domain.topic_type.model.TopicTypeEntity;
import com.example.englishmaster_be.domain.topic_type.service.ITopicTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Topic Type")
@RestController
@RequestMapping("/topic-type")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TopicTypeController {

    ITopicTypeService topicTypeService;

    @GetMapping("/{topicTypeId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get single topic type.",
            description = "Get single topic type."
    )
    public TopicTypeResponse getSingleTopicType(@PathVariable("topicTypeId") UUID topicTypeId) {
        TopicTypeEntity topicType = topicTypeService.getTopicTypeToId(topicTypeId);
        return TopicTypeMapper.INTANCE.toTopicTypeResponse(topicType);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get single topic type.",
            description = "Get single topic type."
    )
    public List<TopicTypeResponse> getListTopicType() {
        List<TopicTypeEntity> topicTypes = topicTypeService.getAllTopicTypes();
        return TopicTypeMapper.INTANCE.toTopicTypeResponseList(topicTypes);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create single topic type.",
            description = "Create single topic type."
    )
    public TopicTypeKeyResponse createTopicType(@RequestBody @Valid CreateTopicTypeRequest request) {
        return topicTypeService.createTopicType(request);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update single topic type.",
            description = "Update single topic type."
    )
    public TopicTypeKeyResponse updateTopicType(@RequestBody @Valid UpdateTopicTypeRequest request) {
        return topicTypeService.updateTopicType(request);
    }

    @DeleteMapping("/{topicTypeId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update single topic type.",
            description = "Update single topic type."
    )
    public void deleteTopicType(@PathVariable("topicTypeId") UUID topicTypeId) {
        topicTypeService.deleteTopicType(topicTypeId);
    }


}
