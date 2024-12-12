package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Configuration.global.annotation.MessageResponse;
import com.example.englishmaster_be.Model.Request.FlashCard.FlashCardWordRequest;
import com.example.englishmaster_be.Mapper.FlashCardWordMapper;
import com.example.englishmaster_be.Model.Response.FlashCardWordResponse;
import com.example.englishmaster_be.Service.*;
import com.example.englishmaster_be.entity.FlashCardWordEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
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
    @MessageResponse("Delete flashcard word successfully")
    public void removeWord(@PathVariable UUID flashCardWordId){

        flashCardWordService.delete(flashCardWordId);
    }

    @PostMapping(value = "/{flashCardId:.+}/addWordToFlashCard", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Create word for flashcard successfully")
    public FlashCardWordResponse addWordToFlashCard(@PathVariable UUID flashCardId, @ModelAttribute FlashCardWordRequest flashCardWordRequest){

        flashCardWordRequest.setFlashCardId(flashCardId);

        FlashCardWordEntity flashCardWord = flashCardWordService.saveFlashCardWord(flashCardWordRequest);

        return FlashCardWordMapper.INSTANCE.toFlashCardWordResponse(flashCardWord);
    }


    @PutMapping(value = "/{flashCardWordId:.+}/updateWord", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @MessageResponse("Update flashcard word successfully")
    public FlashCardWordResponse updateWord(@PathVariable UUID flashCardWordId, @ModelAttribute FlashCardWordRequest flashCardWordRequest){

        flashCardWordRequest.setFlashCardWordId(flashCardWordId);

        FlashCardWordEntity flashCardWord = flashCardWordService.saveFlashCardWord(flashCardWordRequest);

        return FlashCardWordMapper.INSTANCE.toFlashCardWordResponse(flashCardWord);
    }

    @GetMapping("/searchByWord")
    @MessageResponse("Show list flashcard word successfully")
    public List<String> searchFlashCardByWord(@RequestParam(value = "query") String query) {

        return flashCardWordService.searchByFlashCardWord(query);
    }
}
