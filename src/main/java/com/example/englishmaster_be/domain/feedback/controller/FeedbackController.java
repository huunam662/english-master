package com.example.englishmaster_be.domain.feedback.controller;

<<<<<<< HEAD
import com.example.englishmaster_be.common.dto.req.PageOptionsReq;
=======
import com.example.englishmaster_be.shared.dto.response.FilterResponse;
import com.example.englishmaster_be.common.constant.sort.FeedbackSortBy;
>>>>>>> 197ed81940903ab14a285d25c6aed6f94b8e649c

import com.example.englishmaster_be.common.dto.res.PageInfoRes;
import com.example.englishmaster_be.domain.feedback.dto.res.FeedbackPageRes;
import com.example.englishmaster_be.domain.feedback.dto.view.IFeedbackPageView;
import com.example.englishmaster_be.domain.feedback.service.IFeedbackService;
import com.example.englishmaster_be.domain.feedback.dto.req.FeedbackReq;
import com.example.englishmaster_be.domain.feedback.mapper.FeedbackMapper;
import com.example.englishmaster_be.domain.feedback.dto.res.FeedbackRes;
import com.example.englishmaster_be.domain.feedback.model.FeedbackEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Feedback")
@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    private final IFeedbackService feedbackService;

<<<<<<< HEAD
    public FeedbackController(IFeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping(value = "/page")
    @PreAuthorize("hasRole('ADMIN')")
    public PageInfoRes<FeedbackPageRes> getPageFeedback(@ModelAttribute @Valid PageOptionsReq optionsReq){
        Page<IFeedbackPageView> feedbackPageViews = feedbackService.getPageFeedback(optionsReq);
        List<FeedbackPageRes> feedbackPageResList = FeedbackMapper.INSTANCE.toFeedbackPageResList(feedbackPageViews.getContent());
        Page<FeedbackPageRes> feedbackPageRes = new PageImpl<>(feedbackPageResList, feedbackPageViews.getPageable(), feedbackPageViews.getTotalElements());
        return new PageInfoRes<>(feedbackPageRes);
=======

    @GetMapping(value = "/listFeedbackAdmin")
    @PreAuthorize("hasRole('ADMIN')")
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
>>>>>>> 197ed81940903ab14a285d25c6aed6f94b8e649c
    }

    @PostMapping(value = "/createFeedback" )
    @PreAuthorize("hasRole('ADMIN')")
<<<<<<< HEAD
    public FeedbackRes createFeedback(
            @RequestBody @Valid FeedbackReq feedbackRequest
=======
    public FeedbackResponse createFeedback(
            @RequestBody @Valid FeedbackRequest feedbackRequest
>>>>>>> 197ed81940903ab14a285d25c6aed6f94b8e649c
    ){

        FeedbackEntity feedback = feedbackService.saveFeedback(feedbackRequest);

        return FeedbackMapper.INSTANCE.toFeedbackResponse(feedback);
    }

    @PatchMapping (value = "/{feedbackId:.+}/enableFeedback")
    @PreAuthorize("hasRole('ADMIN')")
    public void enableFeedback(
            @PathVariable("feedbackId") UUID feedbackId,
            @RequestParam(defaultValue = "true") Boolean enable
    ){
        
        feedbackService.enableFeedback(feedbackId, enable);
    }

    @PatchMapping(value = "/{feedbackId:.+}/updateFeedback")
    @PreAuthorize("hasRole('ADMIN')")
<<<<<<< HEAD
    public FeedbackRes updateFeedback(
=======
    public FeedbackResponse updateFeedback(
>>>>>>> 197ed81940903ab14a285d25c6aed6f94b8e649c
            @PathVariable("feedbackId") UUID feedbackId,
            @RequestBody @Valid FeedbackReq feedbackRequest
    ){

        feedbackRequest.setFeedbackId(feedbackId);

        FeedbackEntity feedback = feedbackService.saveFeedback(feedbackRequest);

        return FeedbackMapper.INSTANCE.toFeedbackResponse(feedback);
    }

    @DeleteMapping(value = "/{feedbackId:.+}/deleteFeedback")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteFeedback(@PathVariable("feedbackId") UUID feedbackId){

        feedbackService.deleteFeedback(feedbackId);
    }
}
