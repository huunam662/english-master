package com.example.englishmaster_be.domain.feedback.controller;

import com.example.englishmaster_be.common.dto.req.PageOptionsReq;

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
    }

    @PostMapping(value = "/createFeedback" )
    @PreAuthorize("hasRole('ADMIN')")
    public FeedbackRes createFeedback(
            @RequestBody @Valid FeedbackReq feedbackRequest
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
    public FeedbackRes updateFeedback(
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
