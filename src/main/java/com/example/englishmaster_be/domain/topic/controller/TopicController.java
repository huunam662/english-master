package com.example.englishmaster_be.domain.topic.controller;

import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.common.dto.response.FilterResponse;
import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelQuestionListResponse;
import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelTopicResponse;
import com.example.englishmaster_be.domain.question.dto.response.QuestionPartResponse;
import com.example.englishmaster_be.domain.question.dto.response.QuestionResponse;
import com.example.englishmaster_be.domain.topic.service.ITopicService;
import com.example.englishmaster_be.converter.CommentConverter;
import com.example.englishmaster_be.converter.TopicConverter;
import com.example.englishmaster_be.domain.question.dto.request.QuestionRequest;
import com.example.englishmaster_be.domain.topic.dto.request.TopicQuestionListRequest;
import com.example.englishmaster_be.domain.topic.dto.request.TopicRequest;
import com.example.englishmaster_be.domain.topic.dto.request.TopicFilterRequest;
import com.example.englishmaster_be.domain.comment.dto.response.CommentResponse;
import com.example.englishmaster_be.domain.part.dto.response.PartResponse;
import com.example.englishmaster_be.domain.topic.dto.response.TopicResponse;
import com.example.englishmaster_be.model.comment.CommentEntity;
import com.example.englishmaster_be.model.topic.TopicEntity;
import io.swagger.v3.oas.annotations.Parameter;
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
import java.time.LocalDate;
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

        return TopicConverter.INSTANCE.toTopicResponse(topic);
    }

    @PostMapping(value = "/create")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Create topic successfully")
    public TopicResponse createTopic(
            @RequestBody TopicRequest topicRequest
    ) {
        
        TopicEntity topic = topicService.saveTopic(topicRequest);

        return TopicConverter.INSTANCE.toTopicResponse(topic);
    }

    @PostMapping(value = "/createTopicByExcelFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Create topic successfully")
    public ExcelTopicResponse createTopicByExcelFile(
            @RequestParam("file") MultipartFile file
    ) {

        return topicService.saveTopicByExcelFile(file);
    }


    @PutMapping(value = "/{topicId:.+}/updateTopicByExcelFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @DefaultMessage("Update topic successfully")
    public ExcelTopicResponse updateTopicByExcelFile(
            @PathVariable UUID topicId,
            @RequestParam("file") MultipartFile file
    ) {

        return topicService.updateTopicByExcelFile(topicId, file);
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

        return TopicConverter.INSTANCE.toTopicResponse(topic);
    }

    @PutMapping(value = "/{topicId:.+}/uploadImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Upload Topic file storage successfully")
    public TopicResponse uploadFileImage(
            @PathVariable UUID topicId,
            @RequestPart("contentData") MultipartFile contentData
    ) {

        TopicEntity topic = topicService.uploadFileImage(topicId, contentData);

        return TopicConverter.INSTANCE.toTopicResponse(topic);
    }

    @DeleteMapping(value = "/{topicId:.+}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Delete Topic successfully")
    public void deleteTopic(@PathVariable UUID topicId) {

        topicService.deleteTopic(topicId);
    }

    @GetMapping(value = "/listTopic")
    @DefaultMessage("Show list Topic successfully")
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

    @DefaultMessage("Get Topic successfully")
    public TopicResponse getTopic(@RequestParam("id") UUID id) {

        TopicEntity topic = topicService.getTopicById(id);

        return TopicConverter.INSTANCE.toTopicResponse(topic);
    }


    @GetMapping(value = "/suggestTopic")
    @DefaultMessage("Show list 5 Topic name successfully")
    public List<String> get5SuggestTopic(@RequestParam(value = "query") String query) {

        return topicService.get5SuggestTopic(query);
    }


    @PostMapping(value = "/{topicId:.+}/addPart")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Add Part to Topic successfully")
    public void addPartToTopic(@PathVariable UUID topicId, @RequestParam UUID partId) {

        topicService.addPartToTopic(topicId, partId);
    }

    @DeleteMapping(value = "/{topicId:.+}/deletePart")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Delete Part to Topic successfully")
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

    @PostMapping(value = "/{topicId:.+}/addQuestionForTopicAndPartByExcelFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Add Questions to Topic successfully")
    public ExcelQuestionListResponse addQuestionForTopicAndPartByExcelFile(
            @PathVariable("topicId") UUID topicId,
            @RequestParam("partNumber") int partNumber,
            @RequestPart("file") MultipartFile file
    ){

        return topicService.addQuestionForTopicAndPartByExcelFile(topicId, partNumber, file);
    }


    @PostMapping(value = "/{topicId:.+}/addQuestionAllPartsToTopicByExcelFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Add Questions to Topic successfully")
    public ExcelQuestionListResponse addQuestionAllPartsToTopicByExcelFile(
            @PathVariable UUID topicId,
            @RequestParam("file") MultipartFile file
    ) {

        return topicService.addQuestionAllPartsToTopicByExcelFile(topicId, file);
    }

    @PostMapping(value = "/{topicId:.+}/addAllPartsToTopicByExcelFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Add Parts to Topic successfully")
    public ExcelTopicResponse addAllPartsToTopicByExcelFile(
            @PathVariable("topicId") UUID topicId,
            @RequestPart("file") MultipartFile file
    ){

        return topicService.addAllPartsForTopicByExcelFile(topicId, file);
    }


    @DeleteMapping(value = "/{topicId:.+}/deleteQuestion")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Delete Question to Topic successfully")
    public void deleteQuestionToTopic(
            @PathVariable UUID topicId,
            @RequestParam UUID questionId
    ) {

        topicService.deleteQuestionToTopic(topicId, questionId);
    }


    @GetMapping(value = "/{topicId:.+}/listPart")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Show Part to Topic successfully")
    public List<PartResponse> getPartToTopic(@PathVariable UUID topicId) {

        return topicService.getPartToTopic(topicId);
    }

    @GetMapping(value = "/{topicId:.+}/listQuestionToPart")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Show Question of Part to Topic successfully")
    public List<QuestionPartResponse> getQuestionOfToTopicPart(
            @PathVariable UUID topicId,
            @RequestParam String partName
    ) {

        return topicService.getQuestionOfToTopicPart(topicId, partName);
    }


    @PatchMapping(value = "/{topicId:.+}/enableTopic")
    @PreAuthorize("hasRole('ADMIN')")
    public void enableTopic(@PathVariable UUID topicId, @RequestParam boolean enable) {

        topicService.enableTopic(topicId, enable);
    }


    @GetMapping(value = "/{topicId:.+}/listComment")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Show list Comment successfully")
    public List<CommentResponse> listComment(@PathVariable UUID topicId) {

        List<CommentEntity> commentEntityList = topicService.listComment(topicId);

        return CommentConverter.INSTANCE.toCommentResponseList(commentEntityList);
    }


    @GetMapping("/searchByStartTime")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Topic retrieved successfully")
    public List<TopicResponse> getTopicByStartTime(
            @RequestParam
            @Parameter(description = "format startDate is 'yyyy-MM-dd'", example = "2025-01-12")
            @DateTimeFormat(pattern ="yyyy-MM-dd")
            LocalDate startDate
    ){

        List<TopicEntity> topicEntityList = topicService.getTopicsByStartTime(startDate);

        return TopicConverter.INSTANCE.toTopicResponseList(topicEntityList);
    }

    @GetMapping("/{topicId}/list-question-from-all-part")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Topic questions all part successfully")
    public List<QuestionPartResponse> getQuestionFromAllPart(
            @PathVariable UUID topicId
    ) {

        return topicService.getQuestionPartListOfTopic(topicId);
    }

}
