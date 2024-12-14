package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Common.dto.response.FilterResponse;
import com.example.englishmaster_be.Common.enums.sort.SortByFeedbackFieldsEnum;
import com.example.englishmaster_be.Configuration.global.annotation.MessageResponse;
import com.example.englishmaster_be.Model.Request.Feedback.FeedbackFilterRequest;
import com.example.englishmaster_be.Model.Request.Feedback.FeedbackRequest;
import com.example.englishmaster_be.Mapper.FeedbackMapper;
import com.example.englishmaster_be.Model.Response.FeedbackResponse;
import com.example.englishmaster_be.entity.FeedbackEntity;
import com.example.englishmaster_be.Service.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Feedback")
@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedbackController {

    IFeedbackService feedbackService;


    @GetMapping(value = "/listFeedbackAdmin")
    @PreAuthorize("hasRole('ADMIN')")
    @MessageResponse("List FeedbackEntity successfully")
    public FilterResponse<?> listFeedbackOfAdmin(
            @RequestParam(value = "page", defaultValue = "1") @Min(1) Integer page,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(100) Integer size,
            @RequestParam(value = "sortBy") SortByFeedbackFieldsEnum sortBy,
            @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction sortDirection,
            @RequestParam(value = "search", defaultValue = "") String search,
            @RequestParam(value = "enable", defaultValue = "false") Boolean isEnable
    ){

        FeedbackFilterRequest filterRequest = FeedbackFilterRequest
                .builder()
                    .page(page)
                    .pageSize(size)
                    .sortBy(sortBy)
                    .direction(sortDirection)
                    .search(search)
                    .isEnable(isEnable)
                .build();

        return feedbackService.getListFeedbackOfAdmin(filterRequest);
    }

    @GetMapping(value = "/listFeedbackUser")
    @MessageResponse("List FeedbackEntity successfully")
    public FilterResponse<?> listFeedbackOfUser(
            @RequestParam(value = "page", defaultValue = "1") @Min(1) Integer page,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(100) Integer size,
            @RequestParam(value = "search", defaultValue = "") String search
    ){

        FeedbackFilterRequest filterRequest = FeedbackFilterRequest
                .builder()
                    .page(page)
                    .pageSize(size)
                    .search(search)
                .build();

        return feedbackService.getListFeedbackOfUser(filterRequest);
    }

    @PostMapping(value = "/createFeedback" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @MessageResponse("Create FeedbackEntity successfully")
    public FeedbackResponse createFeedback(
            @ModelAttribute("contentFeedback") FeedbackRequest feedbackRequest
    ){

        FeedbackEntity feedback = feedbackService.saveFeedback(feedbackRequest);

        return FeedbackMapper.INSTANCE.toFeedbackResponse(feedback);
    }

    @PatchMapping (value = "/{FeedbackId:.+}/enableFeedback")
    @PreAuthorize("hasRole('ADMIN')")
    public void enableFeedback(
            @PathVariable UUID FeedbackId,
            @RequestParam(defaultValue = "true") Boolean enable
    ){
        
        feedbackService.enableFeedback(FeedbackId, enable);
    }

    @PatchMapping(value = "/{feedbackId:.+}/updateFeedback", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @MessageResponse("Update FeedbackEntity successfully")
    public FeedbackResponse updateFeedback(
            @PathVariable UUID feedbackId,
            @ModelAttribute(name = "contentFeedback") FeedbackRequest feedbackRequest
    ){

        feedbackRequest.setFeedbackId(feedbackId);

        FeedbackEntity feedback = feedbackService.saveFeedback(feedbackRequest);

        return FeedbackMapper.INSTANCE.toFeedbackResponse(feedback);
    }

    @DeleteMapping(value = "/{FeedbackId:.+}/deleteFeedback")
    @PreAuthorize("hasRole('ADMIN')")
    @MessageResponse("Delete FeedbackEntity successfully")
    public void deleteFeedback(@PathVariable UUID FeedbackId){

        feedbackService.deleteFeedback(FeedbackId);
    }
}
