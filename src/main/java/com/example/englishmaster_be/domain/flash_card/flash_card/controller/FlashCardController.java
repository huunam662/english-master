package com.example.englishmaster_be.domain.flash_card.flash_card.controller;


import com.example.englishmaster_be.common.dto.request.PageOptionsReq;
import com.example.englishmaster_be.common.dto.response.PageInfoRes;
import com.example.englishmaster_be.domain.flash_card.flash_card.dto.request.FlashCardReq;
import com.example.englishmaster_be.domain.flash_card.flash_card.dto.response.FlashCardRes;
import com.example.englishmaster_be.domain.flash_card.flash_card.dto.response.FlashCardUserRes;
import com.example.englishmaster_be.domain.flash_card.flash_card.mapper.FlashCardMapper;
import com.example.englishmaster_be.domain.flash_card.flash_card.model.FlashCardEntity;
import com.example.englishmaster_be.domain.flash_card.flash_card.service.IFlashCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@Tag(name = "Flash card")
@RestController
@RequestMapping("/flash-card")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FlashCardController {

    IFlashCardService flashCardService;

    @Operation(
            summary = "Get single flash card to flash card id.",
            description = "Get single flash card to flash card id."
    )
    @GetMapping("/{id}")
    public FlashCardRes getSingleFlashCardToId(@PathVariable("id") UUID id) {
        FlashCardEntity flashCard = flashCardService.getSingleFlashCardToId(id);
        return FlashCardMapper.INSTANCE.toFlashCardRes(flashCard);
    }

    @Operation(
            summary = "Get all flash cards.",
            description = "Get all flash cards."
    )
    @GetMapping
    public List<FlashCardRes> getAllFlashCards() {
        List<FlashCardEntity> list = flashCardService.getAllFlashCards();
        return FlashCardMapper.INSTANCE.toFlashCardResList(list);
    }

    @Operation(
            summary = "Get page flash card.",
            description = "Get page flash card."
    )
    @GetMapping("/page")
    public PageInfoRes<FlashCardUserRes> getPageFlashCard(
        @ModelAttribute PageOptionsReq pageOptions
    ) {
        Page<FlashCardEntity> pageFlashCard = flashCardService.getPageFlashCard(pageOptions);
        List<FlashCardUserRes> flashCardUserResList = FlashCardMapper.INSTANCE.toFlashCardUserResList(pageFlashCard.getContent());
        Page<FlashCardUserRes> pageFlashCardRes = new PageImpl<>(flashCardUserResList, pageFlashCard.getPageable(), pageFlashCard.getTotalElements());
        return new PageInfoRes<>(pageFlashCardRes);
    }

    @Operation(
            summary = "Create single flash card.",
            description = "Create single flash card."
    )
    @PostMapping
    public UUID createFlashCard(@RequestBody @Valid FlashCardReq flashCardReq) {
        FlashCardEntity flashCard = flashCardService.createFlashCard(flashCardReq);
        return flashCard.getId();
    }

    @Operation(
            summary = "Update single flash card to flash card id.",
            description = "Update single flash card to flash card id."
    )
    @PutMapping("/{id}")
    public UUID updateFlashCard(
            @PathVariable("id") UUID id,
            @RequestBody @Valid FlashCardReq flashCardReq
    ){
        FlashCardEntity flashCard = flashCardService.updateFlashCard(id, flashCardReq);
        return flashCard.getId();
    }

    @Operation(
            summary = "Delete single flash card to flash card id.",
            description = "Delete single flash card to flash card id."
    )
    @DeleteMapping("/{id}")
    public void deleteFlashCard(@PathVariable("id") UUID id){
        flashCardService.deleteFlashCard(id);
    }

    @Operation(
            summary = "Update status public shared to flash card id.",
            description = "Update status public shared to flash card id."
    )
    @PatchMapping("/{id}")
    public void changePublicShared(
            @PathVariable("id") UUID id,
            @RequestParam(value = "publicShared", defaultValue = "false") boolean publicShared
    ){
        flashCardService.changePublicShared(id, publicShared);
    }


}
