package com.example.englishmaster_be.domain.flash_card.word.controller;

import com.example.englishmaster_be.common.dto.req.PageOptionsReq;
import com.example.englishmaster_be.common.dto.res.PageInfoRes;
import com.example.englishmaster_be.domain.flash_card.word.dto.req.FlashCardWordReq;
import com.example.englishmaster_be.domain.flash_card.word.dto.req.UpdateFlashCardWordReq;
import com.example.englishmaster_be.domain.flash_card.word.dto.res.FlashCardWordPageRes;
import com.example.englishmaster_be.domain.flash_card.word.dto.res.FlashCardWordRes;
import com.example.englishmaster_be.domain.flash_card.word.dto.view.IFlashCardWordPageView;
import com.example.englishmaster_be.domain.flash_card.word.mapper.FlashCardWordMapper;
import com.example.englishmaster_be.domain.flash_card.word.model.FlashCardWordEntity;
import com.example.englishmaster_be.domain.flash_card.word.service.IFlashCardWordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Flash Card Word")
@RequestMapping("/flash-card-word")
public class FlashCardWordController {

    private final IFlashCardWordService flashCardWordService;

    public FlashCardWordController(IFlashCardWordService flashCardWordService) {
        this.flashCardWordService = flashCardWordService;
    }

    @Operation(
            summary = "Get page flash card word.",
            description = "Get page flash card word."
    )
    @GetMapping("/page")
    public PageInfoRes<FlashCardWordPageRes> getPageFlashCardWord(
            @ModelAttribute @Valid PageOptionsReq optionsReq
    ){
        Page<IFlashCardWordPageView> pageFlashCardWord = flashCardWordService.getPageFlashCardWord(optionsReq);
        List<FlashCardWordPageRes> flashCardWordResList = FlashCardWordMapper.INSTANCE.toFlashCardWordPageResList(pageFlashCardWord.getContent());
        Page<FlashCardWordPageRes> pageFlashCardWordUserRes = new PageImpl<>(flashCardWordResList, pageFlashCardWord.getPageable(), pageFlashCardWord.getTotalElements());
        return new PageInfoRes<>(pageFlashCardWordUserRes);
    }

    @Operation(
            summary = "Get page flash card word to flash card id.",
            description = "Get page flash card word to flash card id."
    )
    @GetMapping("/flash-card/{flashCardId}/page")
    public PageInfoRes<FlashCardWordPageRes> getPageFlashCardWordToFlashCardId(
            @PathVariable("flashCardId") UUID flashCardId,
            @ModelAttribute @Valid PageOptionsReq optionsReq
    ){
        Page<IFlashCardWordPageView> pageFlashCardWord = flashCardWordService.getPageFlashCardWordToFlashCardId(flashCardId, optionsReq);
        List<FlashCardWordPageRes> flashCardWordResList = FlashCardWordMapper.INSTANCE.toFlashCardWordPageResList(pageFlashCardWord.getContent());
        Page<FlashCardWordPageRes> pageFlashCardWordUserRes = new PageImpl<>(flashCardWordResList, pageFlashCardWord.getPageable(), pageFlashCardWord.getTotalElements());
        return new PageInfoRes<>(pageFlashCardWordUserRes);
    }

    @Operation(
            summary = "Get single flash card word to flash card word id.",
            description = "Get single flash card word to flash card word id."
    )
    @GetMapping("/{id}")
    public FlashCardWordRes getSingleFlashCardWordToId(@PathVariable("id") UUID id) {
        FlashCardWordEntity flashCardWord = flashCardWordService.getSingleFlashCardWordToId(id);
        return FlashCardWordMapper.INSTANCE.toFlashCardWordRes(flashCardWord);
    }

    @Operation(
            summary = "Get all flash card word.",
            description = "Get all flash card word."
    )
    @GetMapping
    public List<FlashCardWordRes> getAllFlashCardWords() {
        List<FlashCardWordEntity> flashCardWords = flashCardWordService.getAllFlashCardWords();
        return FlashCardWordMapper.INSTANCE.toFlashCardWordResList(flashCardWords);
    }

    @Operation(
            summary = "Get all flash card word to flash card id.",
            description = "Get all flash card word to flash card id."
    )
    @GetMapping("/flash-card/{flashCardId}")
    public List<FlashCardWordRes> getAllFlashCardWordsToFlashCardId(@PathVariable("flashCardId") UUID flashCardId){
        List<FlashCardWordEntity> flashCardWords = flashCardWordService.getAllFlashCardWordsToFlashCardId(flashCardId);
        return FlashCardWordMapper.INSTANCE.toFlashCardWordResList(flashCardWords);
    }

    @Operation(
            summary = "Create all flash card word for single flash card to flash card id.",
            description = "Create all flash card word for single flash card to flash card id."
    )
    @PostMapping("/flash-card/{flashCardId}")
    public List<UUID> createAllFlashCardWordToFlashCardId(
            @PathVariable("flashCardId") UUID flashCardId,
            @RequestBody @Valid List<FlashCardWordReq> body
    ){
        List<FlashCardWordEntity> flashCardWords = flashCardWordService.createAllFlashCardWordToFlashCardId(flashCardId, body);
        return flashCardWords.stream().map(FlashCardWordEntity::getId).toList();
    }

    @Operation(
            summary = "Update single flash card word to flash card word id.",
            description = "Update single flash card word to flash card word id."
    )
    @PutMapping("/{id}")
    public UUID updateSingleFlashCardWordToId(
            @PathVariable("id") UUID id,
            @RequestBody @Valid FlashCardWordReq body
    ){
        FlashCardWordEntity flashCardWord = flashCardWordService.updateSingleFlashCardWordToId(id, body);
        return flashCardWord.getId();
    }

    @Operation(
            summary = "Update all flash card word.",
            description = "Update all flash card word."
    )
    @PutMapping
    public List<UUID> updateAllFlashCardWords(@RequestBody @Valid List<UpdateFlashCardWordReq> body) {
        List<FlashCardWordEntity> flashCardWords = flashCardWordService.updateAllFlashCardWords(body);
        return flashCardWords.stream().map(FlashCardWordEntity::getId).toList();
    }

    @Operation(
            summary = "Update image for flash card word to flash card word id.",
            description = "Update image for flash card word to flash card word id."
    )
    @PatchMapping("/{id}/image")
    public UUID updateImageFlashCardWordToId(
            @PathVariable("id") UUID id,
            @RequestBody @Valid String imageUrl
    ) throws BadRequestException {
        FlashCardWordEntity flashCardWord = flashCardWordService.updateImageFlashCardWordToId(id, imageUrl);
        return flashCardWord.getId();
    }

    @Operation(
            summary = "Delete single flash card word to flash card word id.",
            description = "Delete single flash card word to flash card word id."
    )
    @DeleteMapping("/{id}")
    public void deleteSingleFlashCardWordToId(@PathVariable("id") UUID id) {
        flashCardWordService.deleteSingleFlashCardWordToId(id);
    }

    @Operation(
            summary = "Delete all flash card word to flash card id.",
            description = "Delete all flash card word to flash card id."
    )
    @DeleteMapping("/flash-card/{flashCardId}")
    public void deleteAllFlashCardWordsToFlashCardId(@PathVariable("flashCardId") UUID flashCardId) {
        flashCardWordService.deleteAllFlashCardWordsToFlashCardId(flashCardId);
    }

    @Operation(
            summary = "Delete all flash card word.",
            description = "Delete all flash card word."
    )
    @DeleteMapping
    public void deleteAllFlashCardWordsToIds(@RequestBody List<UUID> flashCardWordIds){
        flashCardWordService.deleteAllFlashCardWordsToIds(flashCardWordIds);
    }



}
