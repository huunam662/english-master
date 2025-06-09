package com.example.englishmaster_be.domain.feedback.controller;

import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.shared.dto.response.FilterResponse;
import com.example.englishmaster_be.common.constant.sort.FeedbackSortBy;

import com.example.englishmaster_be.domain.feedback.service.IFeedbackService;
import com.example.englishmaster_be.domain.feedback.dto.request.FeedbackFilterRequest;
import com.example.englishmaster_be.domain.feedback.dto.request.FeedbackRequest;
import com.example.englishmaster_be.domain.feedback.mapper.FeedbackMapper;
import com.example.englishmaster_be.domain.feedback.dto.response.FeedbackResponse;
import com.example.englishmaster_be.domain.feedback.model.FeedbackEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
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
    @DefaultMessage("List successfully")
    public FilterResponse<?> listFeedbackOfAdmin(
            @RequestParam(value = "page", defaultValue = "1") @Min(1) Integer page,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(100) Integer size,
            @RequestParam(value = "sortBy", defaultValue = "None") FeedbackSortBy sortBy,
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
    @DefaultMessage("List successfully")
    public FilterResponse<?> listFeedbackOfUser(
            @RequestParam(value = "page", defaultValue = "1") @Min(1) Integer page,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(100) Integer size,
            @RequestParam(value = "search", defaultValue = "") String search,
            @RequestParam(value = "sortBy", defaultValue = "None") FeedbackSortBy sortBy,
              @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction sortDirection
    ){

        FeedbackFilterRequest filterRequest = FeedbackFilterRequest
                .builder()
                    .page(page)
                    .pageSize(size)
                    .sortBy(sortBy)
                    .direction(sortDirection)
                    .search(search)
                .build();

        return feedbackService.getListFeedbackOfUser(filterRequest);
    }

    @PostMapping(value = "/createFeedback" )
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Feedback successful.")
    public FeedbackResponse createFeedback(
            @RequestBody @Valid FeedbackRequest feedbackRequest
    ){

        FeedbackEntity feedback = feedbackService.saveFeedback(feedbackRequest);

        return FeedbackMapper.INSTANCE.toFeedbackResponse(feedback);
    }

    @PatchMapping (value = "/{feedbackId:.+}/enableFeedback")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Save successfully")
    public void enableFeedback(
            @PathVariable("feedbackId") UUID feedbackId,
            @RequestParam(defaultValue = "true") Boolean enable
    ){
        
        feedbackService.enableFeedback(feedbackId, enable);
    }

    @PatchMapping(value = "/{feedbackId:.+}/updateFeedback")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Save successfully")
    public FeedbackResponse updateFeedback(
            @PathVariable("feedbackId") UUID feedbackId,
            @RequestBody @Valid FeedbackRequest feedbackRequest
    ){

        feedbackRequest.setFeedbackId(feedbackId);

        FeedbackEntity feedback = feedbackService.saveFeedback(feedbackRequest);

        return FeedbackMapper.INSTANCE.toFeedbackResponse(feedback);
    }

    @DeleteMapping(value = "/{feedbackId:.+}/deleteFeedback")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Delete successfully")
    public void deleteFeedback(@PathVariable("feedbackId") UUID feedbackId){

        feedbackService.deleteFeedback(feedbackId);
    }
}
