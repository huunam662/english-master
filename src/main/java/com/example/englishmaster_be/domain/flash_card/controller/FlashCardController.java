package com.example.englishmaster_be.domain.flash_card.controller;


import com.example.englishmaster_be.domain.flash_card.dto.request.FlashCardFilterRequest;
import com.example.englishmaster_be.domain.flash_card.dto.response.FlashCardKeyResponse;
import com.example.englishmaster_be.domain.flash_card.dto.response.FlashCardUserResponse;
import com.example.englishmaster_be.domain.flash_card.service.IFlashCardService;
import com.example.englishmaster_be.domain.flash_card.dto.request.FlashCardRequest;
import com.example.englishmaster_be.domain.flash_card.dto.response.FlashCardResponse;
import com.example.englishmaster_be.shared.dto.response.FilterResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Flash card")
@RestController
@RequestMapping("/flashCard")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FlashCardController {

    IFlashCardService flashCardService;


    @GetMapping("/{flashCardId}/user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(
            summary = "Get single flash card to user.",
            description = "Get single flash card to user."
    )
    public FlashCardUserResponse getFlashCardToUser(@PathVariable("flashCardId") UUID flashCardId) {

        return flashCardService.getFlashCardResponseToUserById(flashCardId);
    }

    @GetMapping(value = "/user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(
            summary = "Get flash card to user with filter on page and page size,...",
            description = "Get flash card to user with filter on page and page size,..."
    )
    public FilterResponse<?> listFlashCardUser(@ModelAttribute FlashCardFilterRequest filterRequest){

        return flashCardService.getListFlashCardUser(filterRequest);
    }


    @GetMapping
    @Operation(
            summary = "Get flash card with filter on page and page size,...",
            description = "Get flash card with filter on page and page size,..."
    )
    public FilterResponse<?> listFlashCard(@ModelAttribute FlashCardFilterRequest filterRequest){

        return flashCardService.getListFlashCard(filterRequest);
    }

    @PostMapping(value = "/user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(
            summary = "Create new flash card to user.",
            description = "Create new flash card to user."
    )
    public FlashCardKeyResponse addFlashCardUser(
            @RequestBody FlashCardRequest flashCardRequest
    ){

        return flashCardService.createFlashCard(flashCardRequest);
    }

    @PutMapping(value = "/{flashCardId:.+}/user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(
            summary = "Update existed flash card to user.",
            description = "Update existed flash card to user."
    )
    public FlashCardKeyResponse updateFlashCardUser(
            @PathVariable("flashCardId") UUID flashCardId,
            @ModelAttribute FlashCardRequest flashCardRequest
    ){

        return flashCardService.updateFlashCard(flashCardId, flashCardRequest);
    }

    @DeleteMapping(value = "/{flashCardId:.+}/user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(
            summary = "Delete existed flash card to user.",
            description = "Delete existed flash card to user."
    )
    public void removeFlashCardUser(@PathVariable("flashCardId") UUID flashCardId){

        flashCardService.deleteFlashCard(flashCardId);
    }

    @DeleteMapping(value = "/{flashCardId:.+}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete existed flash card.",
            description = "Delete existed flash card."
    )
    public void removeFlashCard(@PathVariable("flashCardId") UUID flashCardId){

        flashCardService.deleteFlashCard(flashCardId);
    }

    @GetMapping("/{flashCardId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(
            summary = "Get single flash card.",
            description = "Get single flash card."
    )
    public FlashCardResponse getFlashCard(@PathVariable("flashCardId") UUID flashCardId) {

        return flashCardService.getFlashCardResponseById(flashCardId);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create new flash card.",
            description = "Create new flash card."
    )
    public FlashCardKeyResponse addFlashCard(
            @RequestBody FlashCardRequest flashCardRequest
    ) {

        return flashCardService.createFlashCard(flashCardRequest);
    }


    @PutMapping("/{flashCardId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update flash card.",
            description = "Update new flash card."
    )
    public FlashCardKeyResponse updateFlashCard(
            @PathVariable("flashCardId") UUID flashCardId,
            @RequestBody FlashCardRequest flashCardRequest
    ) {

        return flashCardService.updateFlashCard(flashCardId, flashCardRequest);
    }
}
