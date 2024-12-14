package com.example.englishmaster_be.controller;


import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.model.request.FlashCard.FlashCardRequest;
import com.example.englishmaster_be.mapper.FlashCardMapper;
import com.example.englishmaster_be.model.response.FlashCardListWordResponse;
import com.example.englishmaster_be.model.response.FlashCardResponse;
import com.example.englishmaster_be.entity.FlashCardEntity;
import com.example.englishmaster_be.service.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
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
    @DefaultMessage("Show list flashcard word successfully")
    public FlashCardListWordResponse getWordToFlashCard(@PathVariable UUID flashCardId){

        FlashCardEntity flashCard = flashCardService.getFlashCardById(flashCardId);

        return FlashCardMapper.INSTANCE.toFlashCardListWordResponse(flashCard);
    }


    @GetMapping(value = "/listFlashCardUser")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Show list flashcard successfully")
    public List<FlashCardResponse> listFlashCardUser(){

        List<FlashCardEntity> flashCardList = flashCardService.getListFlashCardByCurrentUser();

        return FlashCardMapper.INSTANCE.toFlashCardResponseList(flashCardList);
    }


    @PostMapping(value = "/addFlashCardUser", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Create flashcard successfully")
    public FlashCardResponse addFlashCardUser(@ModelAttribute FlashCardRequest flashCardRequest){

        FlashCardEntity flashCard = flashCardService.saveFlashCard(flashCardRequest);

        return FlashCardMapper.INSTANCE.toFlashCardResponse(flashCard);
    }

    @PutMapping(value = "/{flashCardId:.+}/updateFlashCard", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Update flashcard successfully")
    public FlashCardResponse updateFlashCard(@PathVariable UUID flashCardId, @ModelAttribute FlashCardRequest flashCardRequest){

        flashCardRequest.setFlashCardId(flashCardId);

        FlashCardEntity flashCard = flashCardService.saveFlashCard(flashCardRequest);

        return FlashCardMapper.INSTANCE.toFlashCardResponse(flashCard);
    }

    @DeleteMapping(value = "/{flashCardId:.+}/removeFlashCard")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Delete flashcard successfully")
    public void removeWord(@PathVariable UUID flashCardId){

        flashCardService.delete(flashCardId);
    }

}
