package com.example.englishmaster_be.domain.topic_type.controller;

import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.domain.topic_type.dto.response.TopicTypeResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

public interface ITopicTypeController {

    @GetMapping("/{topicTypeId}")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Get topic type successful.")
    @Operation(
            summary = "Get single topic type.",
            description = "Get single topic type."
    )
    TopicTypeResponse getSingleTopicType(@PathVariable UUID topicTypeId);

}
