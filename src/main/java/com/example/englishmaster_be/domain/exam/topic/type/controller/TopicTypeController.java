package com.example.englishmaster_be.domain.exam.topic.type.controller;

import com.example.englishmaster_be.common.dto.req.PageOptionsReq;
import com.example.englishmaster_be.common.dto.res.PageInfoRes;
import com.example.englishmaster_be.domain.exam.topic.type.dto.req.CreateTopicTypeReq;
import com.example.englishmaster_be.domain.exam.topic.type.dto.req.UpdateTopicTypeReq;
import com.example.englishmaster_be.domain.exam.topic.type.dto.res.TopicTypeKeyRes;
import com.example.englishmaster_be.domain.exam.topic.type.dto.res.TopicTypePageRes;
import com.example.englishmaster_be.domain.exam.topic.type.dto.res.TopicTypeFullRes;
import com.example.englishmaster_be.domain.exam.topic.type.dto.view.ITopicTypePageView;
import com.example.englishmaster_be.domain.exam.topic.type.mapper.TopicTypeMapper;
import com.example.englishmaster_be.domain.exam.topic.type.model.TopicTypeEntity;
import com.example.englishmaster_be.domain.exam.topic.type.service.ITopicTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Topic Type")
@RestController
@RequestMapping("/topic-type")
public class TopicTypeController {

    private final ITopicTypeService topicTypeService;

    public TopicTypeController(ITopicTypeService topicTypeService) {
        this.topicTypeService = topicTypeService;
    }

    @GetMapping("/{topicTypeId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get single topic type.",
            description = "Get single topic type."
    )
    public TopicTypeFullRes getSingleTopicType(@PathVariable("topicTypeId") UUID topicTypeId) {
        TopicTypeEntity topicType = topicTypeService.getTopicTypeToId(topicTypeId);
        return TopicTypeMapper.INTANCE.toTopicTypeResponse(topicType);
    }

    @GetMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get page topic type.",
            description = "Get page topic type."
    )
    public PageInfoRes<TopicTypePageRes> getPageTopicType(@ModelAttribute @Valid PageOptionsReq optionsReq){
        Page<ITopicTypePageView> topicTypePage = topicTypeService.getPageTopicType(optionsReq);
        List<TopicTypePageRes> topicTypePageResList = TopicTypeMapper.INTANCE.toTopicTypePageResList(topicTypePage.getContent());
        Page<TopicTypePageRes> topicTypePageRes = new PageImpl<>(topicTypePageResList, topicTypePage.getPageable(), topicTypePage.getTotalElements());
        return new PageInfoRes<>(topicTypePageRes);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get single topic type.",
            description = "Get single topic type."
    )
    public List<TopicTypeFullRes> getListTopicType() {
        List<TopicTypeEntity> topicTypes = topicTypeService.getAllTopicTypes();
        return TopicTypeMapper.INTANCE.toTopicTypeResponseList(topicTypes);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create single topic type.",
            description = "Create single topic type."
    )
    public TopicTypeKeyRes createTopicType(@RequestBody @Valid CreateTopicTypeReq request) {
        return topicTypeService.createTopicType(request);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update single topic type.",
            description = "Update single topic type."
    )
    public TopicTypeKeyRes updateTopicType(@RequestBody @Valid UpdateTopicTypeReq request) {
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
