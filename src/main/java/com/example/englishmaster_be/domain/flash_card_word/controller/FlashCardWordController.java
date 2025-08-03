package com.example.englishmaster_be.domain.flash_card_word.controller;


import com.example.englishmaster_be.domain.flash_card_word.service.IFlashCardWordService;
import com.example.englishmaster_be.domain.flash_card_word.dto.request.FlashCardWordRequest;
import com.example.englishmaster_be.domain.flash_card_word.mapper.FlashCardWordMapper;
import com.example.englishmaster_be.domain.flash_card_word.dto.response.FlashCardWordResponse;
import com.example.englishmaster_be.domain.flash_card_word.model.FlashCardWordEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;


@Tag(name = "Flash card word")
@RestController
@RequestMapping("/flashCardWord")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FlashCardWordController {

    IFlashCardWordService flashCardWordService;


    @DeleteMapping(value = "/{flashCardWordId:.+}/removeWord")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public void removeWord(@PathVariable("flashCardWordId") UUID flashCardWordId){

        flashCardWordService.delete(flashCardWordId);
    }

    @PostMapping(value = "/{flashCardId:.+}/addWordToFlashCard")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public FlashCardWordResponse addWordToFlashCard(
            @PathVariable("flashCardId") UUID flashCardId,
            @RequestBody FlashCardWordRequest flashCardWordRequest
    ){

        flashCardWordRequest.setFlashCardId(flashCardId);

        FlashCardWordEntity flashCardWord = flashCardWordService.saveFlashCardWord(flashCardWordRequest);

        return FlashCardWordMapper.INSTANCE.toFlashCardWordResponse(flashCardWord);
    }


    @PutMapping(value = "/{flashCardWordId:.+}/updateWord")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public FlashCardWordResponse updateWord(
            @PathVariable("flashCardWordId") UUID flashCardWordId,
            @RequestBody FlashCardWordRequest flashCardWordRequest
    ){

        flashCardWordRequest.setFlashCardWordId(flashCardWordId);

        FlashCardWordEntity flashCardWord = flashCardWordService.saveFlashCardWord(flashCardWordRequest);

        return FlashCardWordMapper.INSTANCE.toFlashCardWordResponse(flashCardWord);
    }

    @GetMapping("/searchByWord")
    public List<String> searchFlashCardByWord(@RequestParam(value = "query") String query) {

        return flashCardWordService.searchByFlashCardWord(query);
    }
}
