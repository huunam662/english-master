package com.example.englishmaster_be.domain.flash_card.feedback.controller;

import com.example.englishmaster_be.common.dto.req.PageOptionsReq;
import com.example.englishmaster_be.common.dto.res.PageInfoRes;
import com.example.englishmaster_be.domain.flash_card.feedback.dto.req.FlashCardFeedbackReq;
import com.example.englishmaster_be.domain.flash_card.feedback.dto.res.FlashCardFeedbackFullRes;
import com.example.englishmaster_be.domain.flash_card.feedback.dto.res.FlashCardFeedbackPageRes;
import com.example.englishmaster_be.domain.flash_card.feedback.dto.view.IFlashCardFeedbackPageView;
import com.example.englishmaster_be.domain.flash_card.feedback.mapper.FlashCardFeedbackMapper;
import com.example.englishmaster_be.domain.flash_card.feedback.model.FlashCardFeedbackEntity;
import com.example.englishmaster_be.domain.flash_card.feedback.service.IFlashCardFeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Flash Card Feedback")
@RequestMapping("/flash-card-feedback")
public class FlashCardFeedbackController {

    private final IFlashCardFeedbackService flashCardFeedbackService;

    public FlashCardFeedbackController(IFlashCardFeedbackService flashCardFeedbackService) {
        this.flashCardFeedbackService = flashCardFeedbackService;
    }

    @GetMapping("/{flashCardFeedbackId}")
    @Operation(
            summary = "Get single flash card feedback to id.",
            description = "Get single flash card feedback to id."
    )
    public FlashCardFeedbackFullRes getSingleFlashCardFeedback(@PathVariable("flashCardFeedbackId") UUID id) {
        FlashCardFeedbackEntity flashCardFeedback = flashCardFeedbackService.getSingleFlashCardFeedback(id);
        return FlashCardFeedbackMapper.INSTANCE.toFlashCardFeedbackFullRes(flashCardFeedback);
    }

    @GetMapping
    @Operation(
            summary = "Get All flash card feedback.",
            description = "Get All flash card feedback."
    )
    public List<FlashCardFeedbackFullRes> getListFlashCardFeedback() {
        List<FlashCardFeedbackEntity> flashCardFeedbacks = flashCardFeedbackService.getListFlashCardFeedback();
        return FlashCardFeedbackMapper.INSTANCE.toFlashCardFeedbackFullResList(flashCardFeedbacks);
    }

    @PostMapping("/flash-card/{flashCardId}")
    @Operation(
            summary = "Create feedback for flash card to flash card id.",
            description = "Create feedback for flash card to flash card id."
    )
    public UUID createFlashCardFeedback(
            @PathVariable("flashCardId") UUID flashCardId,
            @RequestBody @Valid FlashCardFeedbackReq body
    ) {
        FlashCardFeedbackEntity flashCardFeedback = flashCardFeedbackService.createFlashCardFeedback(flashCardId, body);
        return flashCardFeedback.getId();
    }

    @PatchMapping("/content/{flashCardFeedbackId}")
    @Operation(
            summary = "Edit content flash card feedback to id.",
            description = "Edit content flash card feedback to id."
    )
    public UUID editFlashCardFeedbackContent(
            @PathVariable("flashCardFeedbackId") UUID id,
            @RequestBody @Valid FlashCardFeedbackReq body
    ){
        FlashCardFeedbackEntity flashCardFeedback = flashCardFeedbackService.editFlashCardFeedbackContent(id, body);
        return flashCardFeedback.getId();
    }

    @DeleteMapping("/{flashCardFeedbackId}")
    @Operation(
            summary = "Delete single flash card feedback to id.",
            description = "Delete single flash card feedback to id."
    )
    public void deleteSingleFlashCardFeedback(@PathVariable("flashCardFeedbackId") UUID id) {
        flashCardFeedbackService.deleteSingleFlashCardFeedback(id);
    }

    @GetMapping("/page")
    @Operation(
            summary = "Get page flash card feedback.",
            description = "Get page flash card feedback."
    )
    public PageInfoRes<FlashCardFeedbackPageRes> getPageFlashCardFeedback(@ModelAttribute @Valid PageOptionsReq optionsReq){
        Page<IFlashCardFeedbackPageView> pageFlashCard = flashCardFeedbackService.getPageFlashCardFeedback(optionsReq);
        List<FlashCardFeedbackPageRes> flashCardFeedbackResDtos = FlashCardFeedbackMapper.INSTANCE.toFlashCardFeedbackPageResList(pageFlashCard.getContent());
        Page<FlashCardFeedbackPageRes> pageFlashCardFeedbackRes = new PageImpl<>(flashCardFeedbackResDtos, pageFlashCard.getPageable(), pageFlashCard.getTotalElements());
        return new PageInfoRes<>(pageFlashCardFeedbackRes);
    }

    @GetMapping("/flash-card/{flashCardId}/page")
    @Operation(
            summary = "Get page flash card feedback to flash card id.",
            description = "Get page flash card feedback to flash card id."
    )
    public PageInfoRes<FlashCardFeedbackPageRes> getPageFlashCardFeedbackToFlashCardId(
            @PathVariable("flashCardId") UUID flashCardId,
            @ModelAttribute @Valid PageOptionsReq optionsReq
    ){
        Page<IFlashCardFeedbackPageView> pageFlashCard = flashCardFeedbackService.getPageFlashCardFeedbackToFlashCardId(flashCardId, optionsReq);
        List<FlashCardFeedbackPageRes> flashCardFeedbackResDtos = FlashCardFeedbackMapper.INSTANCE.toFlashCardFeedbackPageResList(pageFlashCard.getContent());
        Page<FlashCardFeedbackPageRes> pageFlashCardFeedbackFbRes = new PageImpl<>(flashCardFeedbackResDtos, pageFlashCard.getPageable(), pageFlashCard.getTotalElements());
        return new PageInfoRes<>(pageFlashCardFeedbackFbRes);
    }
}
