package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Configuration.global.annotation.MessageResponse;
import com.example.englishmaster_be.DTO.FlashCard.UpdateFlashCardDTO;
import com.example.englishmaster_be.Model.Response.ExceptionResponseModel;
import com.example.englishmaster_be.DTO.FlashCard.CreateFlashCardDTO;
import com.example.englishmaster_be.DTO.FlashCard.CreateFlashCardWordDTO;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.*;
import com.example.englishmaster_be.Model.Response.ResponseModel;
import com.example.englishmaster_be.Service.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Tag(name = "Flash card")
@RestController
@RequestMapping("/api/flashCard")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FlashCardController {


    IFlashCardService flashCardService;


    @GetMapping(value = "/{flashCardId:.+}/listFlashCardWord")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Show list flashcard word successfully")
    public FlashCardListWordsResponse getWordToFlashCard(@PathVariable UUID flashCardId){

        return flashCardService.getWordToFlashCard(flashCardId);
    }


    @GetMapping(value = "/listFlashCardUser")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Show list flashcard successfully")
    public List<FlashCardResponse> listFlashCardUser(){

        return flashCardService.getListFlashCardUser();
    }


    @PostMapping(value = "/addFlashCardUser", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Create flashcard successfully")
    public FlashCardResponse addFlashCardUser(@ModelAttribute CreateFlashCardDTO createFlashCardDTO){

        return flashCardService.saveFlashCard(createFlashCardDTO);
    }

    @PutMapping(value = "/{flashCardId:.+}/updateFlashCard", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Update flashcard successfully")
    public FlashCardResponse updateFlashCard(@PathVariable UUID flashCardId, @ModelAttribute UpdateFlashCardDTO updateFlashCardDTO){

        updateFlashCardDTO.setFlashCardId(flashCardId);

        return flashCardService.saveFlashCard(updateFlashCardDTO);
    }

    @DeleteMapping(value = "/{flashCardId:.+}/removeFlashCard")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Delete flashcard successfully")
    public void removeWord(@PathVariable UUID flashCardId){

        flashCardService.delete(flashCardId);
    }

}
