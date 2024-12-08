package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Common.dto.response.FilterResponse;
import com.example.englishmaster_be.Configuration.global.annotation.MessageResponse;
import com.example.englishmaster_be.Exception.Error;
import com.example.englishmaster_be.Constant.StatusConstant;
import com.example.englishmaster_be.Model.Response.ExceptionResponseModel;
import com.example.englishmaster_be.Model.Response.ResponseModel;
import com.example.englishmaster_be.Model.Response.excel.CreateQuestionByExcelFileResponse;
import com.example.englishmaster_be.Exception.CustomException;
import com.example.englishmaster_be.Helper.GetExtension;
import com.example.englishmaster_be.DTO.Answer.SaveListAnswerDTO;
import com.example.englishmaster_be.DTO.Question.SaveQuestionDTO;
import com.example.englishmaster_be.DTO.Topic.*;
import com.example.englishmaster_be.DTO.UploadFileDTO;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.*;
import com.example.englishmaster_be.Model.Response.excel.CreateListQuestionByExcelFileResponse;
import com.example.englishmaster_be.Model.Response.excel.CreateTopicByExcelFileResponse;
import com.example.englishmaster_be.Repository.AnswerRepository;
import com.example.englishmaster_be.Repository.ContentRepository;
import com.example.englishmaster_be.Repository.StatusRepository;
import com.example.englishmaster_be.Service.*;
import com.example.englishmaster_be.Service.impl.ContentServiceImpl;
import com.example.englishmaster_be.Service.impl.TopicServiceImpl;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.json.simple.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@Tag(name = "Topic")
@RestController
@RequestMapping("/api/topic")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TopicController {


    ITopicService topicService;


    @GetMapping(value = "/{topicId:.+}/inforTopic")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Show list Topic successfully")
    public TopicResponse getInformationTopic(@PathVariable UUID topicId) {

        Topic topic = topicService.findTopicById(topicId);

        return new TopicResponse(topic);
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @MessageResponse("Create Topic successfully")
    public TopicResponse createTopic(@ModelAttribute SaveTopicDTO saveTopicDTO) {

        return topicService.saveTopic(saveTopicDTO);
    }

    @PostMapping(value = "/createTopicByExcelFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @MessageResponse("Create Topic successfully")
    public TopicResponse createTopicByExcelFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("url") String url
    ) {

        return topicService.saveTopicByExcelFile(file, url);
    }


    @PutMapping(value = "/{topicId:.+}/updateTopic", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @MessageResponse("Update Topic successfully")
    public TopicResponse updateTopic(@PathVariable UUID topicId, @ModelAttribute UpdateTopicDTO updateTopicDTO) {

        updateTopicDTO.setUpdateTopicId(topicId);

        return topicService.saveTopic(updateTopicDTO);
    }

    @PutMapping(value = "/{topicId:.+}/uploadImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @MessageResponse("Upload Topic file successfully")
    public TopicResponse uploadFileImage(@PathVariable UUID topicId, @ModelAttribute UploadFileDTO uploadFileDTO) {

        return topicService.uploadFileImage(topicId, uploadFileDTO);
    }

    @DeleteMapping(value = "/{topicId:.+}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    @MessageResponse("Delete Topic successfully")
    public void deleteTopic(@PathVariable UUID topicId) {

        topicService.deleteTopic(topicId);
    }

    @GetMapping(value = "/listTopic")
    @MessageResponse("Show list Topic successfully")
    public FilterResponse<?> getAllTopic(
            @RequestParam(value = "page", defaultValue = "0") @Min(0) int page,
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
                .size(size)
                .sortBy(sortBy)
                .packId(packId)
                .type(type)
                .sortDirection(sortDirection)
                .build();

        return topicService.getAllTopic(filterRequest);
    }

    @GetMapping("/getTopic")
    @MessageResponse("Get Topic successfully")
    public TopicResponse getTopic(@RequestParam("id") UUID id) {

        return topicService.getTopic(id);
    }


    @GetMapping(value = "/suggestTopic")
    @MessageResponse("Show list 5 Topic name successfully")
    public List<String> get5SuggestTopic(@RequestParam(value = "query") String query) {

        return topicService.get5SuggestTopic(query);
    }


    @PostMapping(value = "/{topicId:.+}/addPart")
    @PreAuthorize("hasRole('ADMIN')")
    @MessageResponse("Add Part to Topic successfully")
    public void addPartToTopic(@PathVariable UUID topicId, @RequestParam UUID partId) {

        topicService.addPartToTopic(topicId, partId);
    }

    @DeleteMapping(value = "/{topicId:.+}/deletePart")
    @PreAuthorize("hasRole('ADMIN')")
    @MessageResponse("Delete Part to Topic successfully")
    public void deletePartToTopic(@PathVariable UUID topicId, @RequestParam UUID partId) {

        topicService.deletePartToTopic(topicId, partId);
    }

    @PostMapping(value = "/{topicId:.+}/addQuestion")
    @PreAuthorize("hasRole('ADMIN')")
    public QuestionResponse addQuestionToTopic(@PathVariable UUID topicId, @ModelAttribute SaveQuestionDTO createQuestionDTO) {

        return topicService.addQuestionToTopic(topicId, createQuestionDTO);
    }

    @PostMapping(value = "/{topicId:.+}/addListQuestion", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public void addListQuestionToTopic(@PathVariable UUID topicId, @ModelAttribute("listQuestion") SaveListQuestionDTO createQuestionDTOList) {

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
    @MessageResponse("Delete Question to Topic successfully")
    public void deleteQuestionToTopic(
            @PathVariable UUID topicId,
            @RequestParam UUID questionId
    ) {

        topicService.deleteQuestionToTopic(topicId, questionId);
    }


    @GetMapping(value = "/{topicId:.+}/listPart")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Show Part to Topic successfully")
    public List<PartResponse> getPartToTopic(@PathVariable UUID topicId) {

        return topicService.getPartToTopic(topicId);
    }

    @GetMapping(value = "/{topicId:.+}/listQuestionToPart")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Show Question of Part to Topic successfully")
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
    @MessageResponse("Show list Comment successfully")
    public List<CommentResponse> listComment(@PathVariable UUID topicId) {

        return topicService.listComment(topicId);
    }


    @GetMapping("searchByStartTime")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Topic retrieved successfully")
    public List<TopicResponse> getTopicByStartTime(
            @RequestParam @DateTimeFormat(pattern ="yyyy-MM-dd") LocalDateTime startDate
    ){

        return topicService.getTopicsByStartTime(startDate);
    }
}
