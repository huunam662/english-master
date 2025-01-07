package com.example.englishmaster_be.domain.topic.controller;

import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.common.dto.response.FilterResponse;

import com.example.englishmaster_be.domain.topic.service.ITopicService;
import com.example.englishmaster_be.mapper.CommentMapper;
import com.example.englishmaster_be.mapper.TopicMapper;
import com.example.englishmaster_be.domain.question.dto.request.QuestionRequest;
import com.example.englishmaster_be.domain.topic.dto.request.TopicQuestionListRequest;
import com.example.englishmaster_be.domain.topic.dto.request.TopicRequest;
import com.example.englishmaster_be.domain.topic.dto.request.TopicFilterRequest;
import com.example.englishmaster_be.domain.comment.dto.response.CommentResponse;
import com.example.englishmaster_be.domain.part.dto.response.PartResponse;
import com.example.englishmaster_be.domain.question.dto.response.QuestionResponse;
import com.example.englishmaster_be.domain.topic.dto.response.TopicResponse;
import com.example.englishmaster_be.model.comment.CommentEntity;
import com.example.englishmaster_be.model.topic.TopicEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.*;


@Tag(name = "Topic")
@RestController
@RequestMapping("/topic")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TopicController {

    ITopicService topicService;


    @GetMapping(value = "/{topicId:.+}/inforTopic")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Show list topic successfully")
    public TopicResponse getInformationTopic(@PathVariable UUID topicId) {

        TopicEntity topic = topicService.getTopicById(topicId);

        return TopicMapper.INSTANCE.toTopicResponse(topic);
    }

    @PostMapping(value = "/create")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Create topic successfully")
    public TopicResponse createTopic(
            @RequestBody TopicRequest topicRequest
    ) {
        
        TopicEntity topic = topicService.saveTopic(topicRequest);

        return TopicMapper.INSTANCE.toTopicResponse(topic);
    }

    @PostMapping(value = "/createTopicByExcelFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Create topic successfully")
    public TopicResponse createTopicByExcelFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "url", required = false) String url
    ) {

        TopicEntity topic = topicService.saveTopicByExcelFile(file, url);

        return TopicMapper.INSTANCE.toTopicResponse(topic);
    }


    @PutMapping(value = "/{topicId:.+}/updateTopicByExcelFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @DefaultMessage("Update topic successfully")
    public TopicResponse updateTopicByExcelFile(
            @PathVariable UUID topicId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("url") String url
    ) {

        TopicEntity topic = topicService.updateTopicByExcelFile(topicId, file, url);

        return TopicMapper.INSTANCE.toTopicResponse(topic);
    }



    @PutMapping(value = "/{topicId:.+}/updateTopic")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Update topic successfully")
    public TopicResponse updateTopic(
            @PathVariable UUID topicId,
            @RequestBody TopicRequest topicRequest
    ) {

        topicRequest.setTopicId(topicId);

        TopicEntity topic = topicService.saveTopic(topicRequest);

        return TopicMapper.INSTANCE.toTopicResponse(topic);
    }

    @PutMapping(value = "/{topicId:.+}/uploadImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Upload TopicEntity file_storage successfully")
    public TopicResponse uploadFileImage(
            @PathVariable UUID topicId,
            @RequestPart("contentData") MultipartFile contentData
    ) {

        TopicEntity topic = topicService.uploadFileImage(topicId, contentData);

        return TopicMapper.INSTANCE.toTopicResponse(topic);
    }

    @DeleteMapping(value = "/{topicId:.+}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Delete TopicEntity successfully")
    public void deleteTopic(@PathVariable UUID topicId) {

        topicService.deleteTopic(topicId);
    }

    @GetMapping(value = "/listTopic")
    @DefaultMessage("Show list TopicEntity successfully")
    public FilterResponse<?> getAllTopic(
            @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
            @RequestParam(value = "size", defaultValue = "12") @Min(1) @Max(100) int size,
            @RequestParam(value = "sortBy", defaultValue = "updateAt") String sortBy,
            @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction sortDirection,
            @RequestParam(value = "pack", required = false) UUID packId,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "type", required = false) String type
    ) {

        TopicFilterRequest filterRequest = TopicFilterRequest.builder()
                .page(page)
                .search(search)
                .pageSize(size)
                .sortBy(sortBy)
                .packId(packId)
                .type(type)
                .sortDirection(sortDirection)
                .build();

        return topicService.getAllTopic(filterRequest);
    }

    @GetMapping("/getTopic")
    @DefaultMessage("Get TopicEntity successfully")
    public TopicResponse getTopic(@RequestParam("id") UUID id) {

        TopicEntity topic = topicService.getTopicById(id);

        return TopicMapper.INSTANCE.toTopicResponse(topic);
    }


    @GetMapping(value = "/suggestTopic")
    @DefaultMessage("Show list 5 TopicEntity name successfully")
    public List<String> get5SuggestTopic(@RequestParam(value = "query") String query) {

        return topicService.get5SuggestTopic(query);
    }


    @PostMapping(value = "/{topicId:.+}/addPart")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Add PartEntity to TopicEntity successfully")
    public void addPartToTopic(@PathVariable UUID topicId, @RequestParam UUID partId) {

        topicService.addPartToTopic(topicId, partId);
    }

    @DeleteMapping(value = "/{topicId:.+}/deletePart")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Delete PartEntity to TopicEntity successfully")
    public void deletePartToTopic(@PathVariable UUID topicId, @RequestParam UUID partId) {

        topicService.deletePartToTopic(topicId, partId);
    }

    @PostMapping(value = "/{topicId:.+}/addQuestion")
    @PreAuthorize("hasRole('ADMIN')")
    public QuestionResponse addQuestionToTopic(
            @PathVariable UUID topicId,
            @ModelAttribute QuestionRequest createQuestionDTO
    ) {

        return topicService.addQuestionToTopic(topicId, createQuestionDTO);
    }

    @PostMapping(value = "/{topicId:.+}/addListQuestion")
    @PreAuthorize("hasRole('ADMIN')")
    public void addListQuestionToTopic(
            @PathVariable UUID topicId,
            @ModelAttribute("listQuestion") TopicQuestionListRequest createQuestionDTOList
    ) {

        topicService.addListQuestionToTopic(topicId, createQuestionDTOList);
    }

    @PostMapping(value = "/{topicId:.+}/addListQuestionPart12ToTopicByExcelFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public void addListQuestionPart12ToTopicByExcelFile(
            @PathVariable UUID topicId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("part") int partName
    ) {

        topicService.addListQuestionPart123467ToTopicByExcelFile(topicId, file, partName);
    }

    @PostMapping(value = "/{topicId:.+}/addListQuestionPart34ToTopicByExcelFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public void addListQuestionPart34ToTopicByExcelFile(
            @PathVariable UUID topicId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("part") int partName
    ) {

        topicService.addListQuestionPart123467ToTopicByExcelFile(topicId, file, partName);
    }

    @PostMapping(value = "/{topicId:.+}/addListQuestionPart5ToTopicByExcelFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public void addListQuestionPart5ToTopicByExcelFile(@PathVariable UUID topicId, @RequestParam("file") MultipartFile file) {

        topicService.addListQuestionPart5ToTopicByExcelFile(topicId, file);
    }


    @PostMapping(value = "/{topicId:.+}/addListQuestionPart67ToTopicByExcelFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public void addListQuestionPart67ToTopicByExcelFile(
            @PathVariable UUID topicId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("part") int partName
    ) {

        topicService.addListQuestionPart123467ToTopicByExcelFile(topicId, file, partName);
    }

    @PostMapping(value = "/{topicId:.+}/addAllPartsToTopicByExcelFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public void addAllPartsToTopicByExcelFile(
            @PathVariable UUID topicId,
            @RequestParam("file") MultipartFile file
    ) {

        topicService.addAllPartsToTopicByExcelFile(topicId, file);
    }


    @DeleteMapping(value = "/{topicId:.+}/deleteQuestion")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Delete QuestionEntity to TopicEntity successfully")
    public void deleteQuestionToTopic(
            @PathVariable UUID topicId,
            @RequestParam UUID questionId
    ) {

        topicService.deleteQuestionToTopic(topicId, questionId);
    }


    @GetMapping(value = "/{topicId:.+}/listPart")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Show PartEntity to TopicEntity successfully")
    public List<PartResponse> getPartToTopic(@PathVariable UUID topicId) {

        return topicService.getPartToTopic(topicId);
    }

    @GetMapping(value = "/{topicId:.+}/listQuestionToPart")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Show QuestionEntity of PartEntity to TopicEntity successfully")
    public List<QuestionResponse> getQuestionOfToTopic(@PathVariable UUID topicId, @RequestParam UUID partId) {

        return topicService.getQuestionOfToTopic(topicId, partId);
    }


    @PatchMapping(value = "/{topicId:.+}/enableTopic")
    @PreAuthorize("hasRole('ADMIN')")
    public void enableTopic(@PathVariable UUID topicId, @RequestParam boolean enable) {

        topicService.enableTopic(topicId, enable);
    }


    @GetMapping(value = "/{topicId:.+}/listComment")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Show list CommentEntity successfully")
    public List<CommentResponse> listComment(@PathVariable UUID topicId) {

        List<CommentEntity> commentEntityList = topicService.listComment(topicId);

        return CommentMapper.INSTANCE.toCommentResponseList(commentEntityList);
    }


    @GetMapping("/searchByStartTime")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("TopicEntity retrieved successfully")
    public List<TopicResponse> getTopicByStartTime(
            @RequestParam @DateTimeFormat(pattern ="yyyy-MM-dd") LocalDateTime startDate
    ){

        List<TopicEntity> topicEntityList = topicService.getTopicsByStartTime(startDate);

        return TopicMapper.INSTANCE.toTopicResponseList(topicEntityList);
    }
}
