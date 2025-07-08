package com.example.englishmaster_be.domain.flash_card.feedback.controller;

import com.example.englishmaster_be.common.dto.request.PageOptionsReq;
import com.example.englishmaster_be.common.dto.response.PageInfoRes;
import com.example.englishmaster_be.domain.flash_card.feedback.dto.req.FlashCardFeedbackReq;
import com.example.englishmaster_be.domain.flash_card.feedback.dto.res.FlashCardFeedbackFbRes;
import com.example.englishmaster_be.domain.flash_card.feedback.dto.res.FlashCardFeedbackRes;
import com.example.englishmaster_be.domain.flash_card.feedback.mapper.FlashCardFeedbackMapper;
import com.example.englishmaster_be.domain.flash_card.feedback.model.FlashCardFeedbackEntity;
import com.example.englishmaster_be.domain.flash_card.feedback.service.IFlashCardFeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    public FlashCardFeedbackFbRes getSingleFlashCardFeedback(@PathVariable("flashCardFeedbackId") UUID id) {
        FlashCardFeedbackEntity flashCardFeedback = flashCardFeedbackService.getSingleFlashCardFeedback(id);
        return FlashCardFeedbackMapper.INSTANCE.toFlashCardFeedbackFbRes(flashCardFeedback);
    }

    @GetMapping
    @Operation(
            summary = "Get All flash card feedback.",
            description = "Get All flash card feedback."
    )
    public List<FlashCardFeedbackFbRes> getListFlashCardFeedback() {
        List<FlashCardFeedbackEntity> flashCardFeedbacks = flashCardFeedbackService.getListFlashCardFeedback();
        return FlashCardFeedbackMapper.INSTANCE.toFlashCardFeedbackFbResList(flashCardFeedbacks);
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
    public PageInfoRes<FlashCardFeedbackFbRes> getPageFlashCardFeedback(@ModelAttribute @Valid PageOptionsReq optionsReq){
        Page<FlashCardFeedbackEntity> pageFlashCard = flashCardFeedbackService.getPageFlashCardFeedback(optionsReq);
        List<FlashCardFeedbackFbRes> flashCardFeedbackResDtos = FlashCardFeedbackMapper.INSTANCE.toFlashCardFeedbackFbResList(pageFlashCard.getContent());
        Page<FlashCardFeedbackFbRes> pageFlashCardFeedbackRes = new PageImpl<>(flashCardFeedbackResDtos, pageFlashCard.getPageable(), pageFlashCard.getTotalElements());
        return new PageInfoRes<>(pageFlashCardFeedbackRes);
    }

    @GetMapping("/page/{flashCardId}")
    @Operation(
            summary = "Get page flash card feedback to flash card id.",
            description = "Get page flash card feedback to flash card id."
    )
    public PageInfoRes<FlashCardFeedbackRes> getPageFlashCardFeedbackToFlashCardId(
            @PathVariable("flashCardId") UUID flashCardId,
            @ModelAttribute @Valid PageOptionsReq optionsReq
    ){
        Page<FlashCardFeedbackEntity> pageFlashCard = flashCardFeedbackService.getPageFlashCardFeedbackToFlashCardId(flashCardId, optionsReq);
        List<FlashCardFeedbackRes> flashCardFeedbackResDtos = FlashCardFeedbackMapper.INSTANCE.toFlashCardFeedbackResList(pageFlashCard.getContent());
        Page<FlashCardFeedbackRes> pageFlashCardFeedbackFbRes = new PageImpl<>(flashCardFeedbackResDtos, pageFlashCard.getPageable(), pageFlashCard.getTotalElements());
        return new PageInfoRes<>(pageFlashCardFeedbackFbRes);
    }
}
