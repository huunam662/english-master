package com.example.englishmaster_be.domain.topic.controller;

import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.shared.dto.response.FilterResponse;
import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelQuestionListResponse;
import com.example.englishmaster_be.domain.excel_fill.dto.response.ExcelTopicResponse;
import com.example.englishmaster_be.domain.question.dto.response.QuestionPartResponse;
import com.example.englishmaster_be.domain.question.dto.response.QuestionResponse;
import com.example.englishmaster_be.domain.topic.service.ITopicService;
import com.example.englishmaster_be.domain.comment.mapper.CommentMapper;
import com.example.englishmaster_be.domain.topic.mapper.TopicMapper;
import com.example.englishmaster_be.domain.question.dto.request.QuestionRequest;
import com.example.englishmaster_be.domain.topic.dto.request.TopicQuestionListRequest;
import com.example.englishmaster_be.domain.topic.dto.request.TopicRequest;
import com.example.englishmaster_be.domain.topic.dto.request.TopicFilterRequest;
import com.example.englishmaster_be.domain.comment.dto.response.CommentResponse;
import com.example.englishmaster_be.domain.part.dto.response.PartResponse;
import com.example.englishmaster_be.domain.topic.dto.response.TopicResponse;
import com.example.englishmaster_be.domain.comment.model.CommentEntity;
import com.example.englishmaster_be.domain.topic.model.TopicEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
    public TopicResponse getInformationTopic(@PathVariable("topicId") UUID topicId) {

        TopicEntity topic = topicService.getTopicById(topicId);

        return TopicMapper.INSTANCE.toTopicResponse(topic);
    }

    @PostMapping(value = "/create")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Create topic successfully")
    public TopicResponse createTopic(
            @RequestBody TopicRequest topicRequest
    ) {
        
        TopicEntity topic = topicService.createTopic(topicRequest);

        return TopicMapper.INSTANCE.toTopicResponse(topic);
    }


    @PutMapping(value = "/{topicId:.+}/updateTopic")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Update topic successfully")
    public TopicResponse updateTopic(
            @PathVariable("topicId") UUID topicId,
            @RequestBody TopicRequest topicRequest
    ) {

        TopicEntity topic = topicService.updateTopic(topicId, topicRequest);

        return TopicMapper.INSTANCE.toTopicResponse(topic);
    }


    @DeleteMapping(value = "/{topicId:.+}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Delete Topic successfully")
    public void deleteTopic(@PathVariable UUID topicId) {

        topicService.deleteTopic(topicId);
    }

    @GetMapping(value = "/listTopic")
    @DefaultMessage("Show list Topic successfully")
    public FilterResponse<?> getAllTopic(@ModelAttribute TopicFilterRequest filterRequest) {

        return topicService.filterTopics(filterRequest);
    }

    @GetMapping("/{topicId}")
    @DefaultMessage("Get Topic successfully")
    public TopicResponse getTopic(@PathVariable("topicId") UUID id) {

        TopicEntity topic = topicService.getTopicById(id);

        return TopicMapper.INSTANCE.toTopicResponse(topic);
    }


    @GetMapping(value = "/suggestTopic")
    @DefaultMessage("Show list 5 Topic name successfully")
    public List<String> get5SuggestTopic(@RequestParam(value = "query") String query) {

        return topicService.get5SuggestTopic(query);
    }


    @PostMapping(value = "/{topicId:.+}/addPart")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Add Part to Topic successfully")
    public void addPartToTopic(@PathVariable("topicId") UUID topicId, @RequestParam("partId") UUID partId) {

        topicService.addPartToTopic(topicId, partId);
    }

    @DeleteMapping(value = "/{topicId:.+}/deletePart")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Delete Part to Topic successfully")
    public void deletePartToTopic(@PathVariable("topicId") UUID topicId, @RequestParam("partId") UUID partId) {

        topicService.deletePartToTopic(topicId, partId);
    }


    @DeleteMapping(value = "/{topicId:.+}/deleteQuestion")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Delete Question to Topic successfully")
    public void deleteQuestionToTopic(
            @PathVariable("topicId") UUID topicId,
            @RequestParam("questionId") UUID questionId
    ) {

        topicService.deleteQuestionToTopic(topicId, questionId);
    }


    @GetMapping(value = "/{topicId:.+}/listPart")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Show Part to Topic successfully")
    public List<PartResponse> getPartToTopic(@PathVariable("topicId") UUID topicId) {

        return topicService.getPartToTopic(topicId);
    }

    @GetMapping(value = "/{topicId:.+}/listQuestionToPart")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Show Question of Part to Topic successfully")
    @Operation(
            summary = "Get all question of part at topic by topic id and part name",
            description = "Get all question of part at topic by topicId and partId"
    )
    public List<QuestionPartResponse> getQuestionOfToTopicPart(
            @PathVariable("topicId") UUID topicId,
            @RequestParam("partName") String partName
    ) {

        return topicService.getQuestionOfToTopicPart(topicId, partName);
    }

    @GetMapping(value = "/{topicId:.+}/part-infor")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Show Question of Part to Topic successfully")
    @Operation(
            summary = "Get all question of part at topic by topic id and part id",
            description = "Get all question of part at topic by topic id and part id"
    )
    public List<QuestionPartResponse> getInforOfToTopicPart(
            @PathVariable("topicId") UUID topicId,
            @RequestParam("partId") UUID partId
    ) {

        return topicService.getQuestionOfToTopicPart(topicId, partId);
    }


    @PatchMapping(value = "/{topicId:.+}/enableTopic")
    @PreAuthorize("hasRole('ADMIN')")
    public void enableTopic(@PathVariable("topicId") UUID topicId, @RequestParam("enable") boolean enable) {

        topicService.enableTopic(topicId, enable);
    }

    @GetMapping("/{topicId}/list-question-from-all-part")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Topic questions all part successfully")
    public List<QuestionPartResponse> getQuestionFromAllPart(
            @PathVariable("topicId") UUID topicId
    ) {

        return topicService.getQuestionPartListOfTopic(topicId);
    }

}
