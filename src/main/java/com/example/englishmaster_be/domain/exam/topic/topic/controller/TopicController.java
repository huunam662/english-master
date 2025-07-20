package com.example.englishmaster_be.domain.exam.topic.topic.controller;

import com.example.englishmaster_be.common.dto.req.PageOptionsReq;
import com.example.englishmaster_be.common.dto.res.PageInfoRes;
import com.example.englishmaster_be.domain.exam.topic.topic.dto.res.*;
import com.example.englishmaster_be.domain.exam.topic.topic.dto.view.ITopicPageView;
import com.example.englishmaster_be.domain.exam.topic.topic.mapper.TopicMapper;
import com.example.englishmaster_be.domain.exam.topic.topic.model.TopicEntity;
import com.example.englishmaster_be.domain.exam.topic.topic.service.ITopicService;
import com.example.englishmaster_be.domain.exam.question.dto.res.QuestionPartRes;
import com.example.englishmaster_be.domain.exam.topic.topic.dto.req.TopicReq;
import com.example.englishmaster_be.domain.exam.part.dto.res.PartRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;


@Tag(name = "Topic")
@RestController
@RequestMapping("/topic")
public class TopicController {

    private final ITopicService topicService;

    public TopicController(ITopicService topicService) {
        this.topicService = topicService;
    }

    @GetMapping("/{id}/media")
    public TopicAudioImageRes getTopicAudio(@PathVariable("id") UUID id){
        TopicEntity topic = topicService.getTopicById(id);
        return TopicMapper.INSTANCE.toTopicAudioImageRes(topic);
    }

    @GetMapping(value = "/{topicId:.+}/inforTopic")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public TopicRes getInformationTopic(@PathVariable("topicId") UUID topicId) {

        TopicEntity topic = topicService.getTopicById(topicId);

        return TopicMapper.INSTANCE.toTopicResponse(topic);
    }

    @PostMapping(value = "/create")
    @PreAuthorize("hasRole('ADMIN')")
    public TopicRes createTopic(
            @RequestBody TopicReq topicRequest
    ) {
        
        TopicEntity topic = topicService.createTopic(topicRequest);

        return TopicMapper.INSTANCE.toTopicResponse(topic);
    }


    @PutMapping(value = "/{topicId:.+}/updateTopic")
    @PreAuthorize("hasRole('ADMIN')")
    public TopicRes updateTopic(
            @PathVariable("topicId") UUID topicId,
            @RequestBody TopicReq topicRequest
    ) {

        TopicEntity topic = topicService.updateTopic(topicId, topicRequest);

        return TopicMapper.INSTANCE.toTopicResponse(topic);
    }


    @DeleteMapping(value = "/{topicId:.+}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteTopic(@PathVariable UUID topicId) {

        topicService.deleteTopic(topicId);
    }

    @GetMapping(value = "/page")
    public PageInfoRes<TopicPageRes> getAllTopic(@ModelAttribute PageOptionsReq optionsReq) {
        Page<ITopicPageView> pageTopic = topicService.getPageTopics(optionsReq);
        List<TopicPageRes> topicResList = TopicMapper.INSTANCE.toTopicPageResList(pageTopic.getContent());
        Page<TopicPageRes> pageTopicRes = new PageImpl<>(topicResList, pageTopic.getPageable(), pageTopic.getTotalElements());
        return new PageInfoRes<>(pageTopicRes);
    }

    @GetMapping(value = "/listTopic")
    public PageInfoRes<TopicPageToPackRes> getAllTopicToPack(@RequestParam("packId") UUID packId, @ModelAttribute PageOptionsReq optionsReq) {
        Page<ITopicPageView> pageTopic = topicService.getPageTopicsToPack(packId, optionsReq);
        List<TopicPageToPackRes> topicResList = TopicMapper.INSTANCE.toTopicPageToPackResList(pageTopic.getContent());
        Page<TopicPageToPackRes> pageTopicRes = new PageImpl<>(topicResList, pageTopic.getPageable(), pageTopic.getTotalElements());
        return new PageInfoRes<>(pageTopicRes);
    }

    @GetMapping("/{topicId}")
    public TopicRes getTopic(@PathVariable("topicId") UUID id) {

        TopicEntity topic = topicService.getTopicById(id);

        return TopicMapper.INSTANCE.toTopicResponse(topic);
    }


    @GetMapping(value = "/suggestTopic")
    public List<String> get5SuggestTopic(@RequestParam(value = "query") String query) {

        return topicService.get5SuggestTopic(query);
    }


    @PostMapping(value = "/{topicId:.+}/addPart")
    @PreAuthorize("hasRole('ADMIN')")
    public void addPartToTopic(@PathVariable("topicId") UUID topicId, @RequestParam("partId") UUID partId) {

        topicService.addPartToTopic(topicId, partId);
    }

    @DeleteMapping(value = "/{topicId:.+}/deletePart")
    @PreAuthorize("hasRole('ADMIN')")
    public void deletePartToTopic(@PathVariable("topicId") UUID topicId, @RequestParam("partId") UUID partId) {

        topicService.deletePartToTopic(topicId, partId);
    }


    @GetMapping(value = "/{topicId:.+}/listPart")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<PartRes> getPartToTopic(@PathVariable("topicId") UUID topicId) {

        return topicService.getPartToTopic(topicId);
    }

    @GetMapping(value = "/{topicId:.+}/listQuestionToPart")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(
            summary = "Get all question of part at topic by topic id and part name",
            description = "Get all question of part at topic by topicId and partId"
    )
    public List<QuestionPartRes> getQuestionOfToTopicPart(
            @PathVariable("topicId") UUID topicId,
            @RequestParam("partName") String partName
    ) {

        return topicService.getQuestionOfToTopicPart(topicId, partName);
    }

    @GetMapping(value = "/{topicId:.+}/part-infor")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(
            summary = "Get all question of part at topic by topic id and part id",
            description = "Get all question of part at topic by topic id and part id"
    )
    public List<QuestionPartRes> getInforOfToTopicPart(
            @PathVariable("topicId") UUID topicId,
            @RequestParam("partId") UUID partId
    ) {

        return topicService.getQuestionOfToTopicPart(topicId, partId);
    }

    @PutMapping(value = "/{topicId:.+}/update-from-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update topic information from excel.",
            description = "Update topic information from excel."
    )
    public TopicKeyRes updateTopicInformationFromExcel(
            @PathVariable("topicId") UUID topicId,
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "imageUrl", required = false) String imageUrl,
            @RequestParam(value = "audioUrl", required = false) String audioUrl
    ) throws BadRequestException {

        return topicService.updateTopicToExcel(file, topicId, imageUrl, audioUrl);
    }

    @PatchMapping(value = "/{topicId:.+}/enableTopic")
    @PreAuthorize("hasRole('ADMIN')")
    public void enableTopic(@PathVariable("topicId") UUID topicId, @RequestParam("enable") boolean enable) {

        topicService.enableTopic(topicId, enable);
    }

    @GetMapping("/{topicId}/list-question-from-all-part")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<QuestionPartRes> getQuestionFromAllPart(
            @PathVariable("topicId") UUID topicId
    ) {

        return topicService.getQuestionPartListOfTopic(topicId);
    }


}
