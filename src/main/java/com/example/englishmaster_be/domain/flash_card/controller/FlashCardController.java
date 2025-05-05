package com.example.englishmaster_be.domain.flash_card.controller;


import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.domain.flash_card.service.IFlashCardService;
import com.example.englishmaster_be.domain.flash_card.dto.request.FlashCardRequest;
import com.example.englishmaster_be.converter.FlashCardConverter;
import com.example.englishmaster_be.domain.flash_card_word.dto.response.FlashCardWordListResponse;
import com.example.englishmaster_be.domain.flash_card.dto.response.FlashCardResponse;
import com.example.englishmaster_be.model.flash_card.FlashCardEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Flash card")
@RestController
@RequestMapping("/flashCard")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FlashCardController {


    IFlashCardService flashCardService;


    @GetMapping(value = "/{flashCardId:.+}/listFlashCardWord")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Show list successfully")
    public FlashCardWordListResponse getWordToFlashCard(@PathVariable UUID flashCardId){

        FlashCardEntity flashCard = flashCardService.getFlashCardById(flashCardId);

        return FlashCardConverter.INSTANCE.toFlashCardListWordResponse(flashCard);
    }


    @GetMapping(value = "/listFlashCardUser")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Show list successfully")
    public List<FlashCardResponse> listFlashCardUser(){

        List<FlashCardEntity> flashCardList = flashCardService.getListFlashCardByCurrentUser();

        return FlashCardConverter.INSTANCE.toFlashCardResponseList(flashCardList);
    }


    @PostMapping(value = "/addFlashCardUser")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Save successfully")
    public FlashCardResponse addFlashCardUser(
            @RequestBody FlashCardRequest flashCardRequest
    ){

        FlashCardEntity flashCard = flashCardService.saveFlashCard(flashCardRequest);

        return FlashCardConverter.INSTANCE.toFlashCardResponse(flashCard);
    }

    @PutMapping(value = "/{flashCardId:.+}/updateFlashCard")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Save successfully")
    public FlashCardResponse updateFlashCard(
            @PathVariable UUID flashCardId,
            @ModelAttribute FlashCardRequest flashCardRequest
    ){

        flashCardRequest.setFlashCardId(flashCardId);

        FlashCardEntity flashCard = flashCardService.saveFlashCard(flashCardRequest);

        return FlashCardConverter.INSTANCE.toFlashCardResponse(flashCard);
    }

    @DeleteMapping(value = "/{flashCardId:.+}/removeFlashCard")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Delete successfully")
    public void removeWord(@PathVariable UUID flashCardId){

        flashCardService.delete(flashCardId);
    }

}
