package com.example.englishmaster_be.domain.flash_card_word.contorller;


import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.domain.flash_card_word.service.IFlashCardWordService;
import com.example.englishmaster_be.domain.flash_card_word.dto.request.FlashCardWordRequest;
import com.example.englishmaster_be.mapper.FlashCardWordMapper;
import com.example.englishmaster_be.domain.flash_card_word.dto.response.FlashCardWordResponse;
import com.example.englishmaster_be.model.flash_card_word.FlashCardWordEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


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
    @DefaultMessage("Delete successfully")
    public void removeWord(@PathVariable UUID flashCardWordId){

        flashCardWordService.delete(flashCardWordId);
    }

    @PostMapping(value = "/{flashCardId:.+}/addWordToFlashCard", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Save successfully")
    public FlashCardWordResponse addWordToFlashCard(
            @PathVariable UUID flashCardId,
            @ModelAttribute FlashCardWordRequest flashCardWordRequest,
            @RequestPart(required = false) MultipartFile image
    ){

        flashCardWordRequest.setFlashCardId(flashCardId);
        flashCardWordRequest.setImage(image);

        FlashCardWordEntity flashCardWord = flashCardWordService.saveFlashCardWord(flashCardWordRequest);

        return FlashCardWordMapper.INSTANCE.toFlashCardWordResponse(flashCardWord);
    }


    @PutMapping(value = "/{flashCardWordId:.+}/updateWord", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Save successfully")
    public FlashCardWordResponse updateWord(
            @PathVariable UUID flashCardWordId,
            @ModelAttribute FlashCardWordRequest flashCardWordRequest,
            @RequestPart(required = false) MultipartFile image
    ){

        flashCardWordRequest.setFlashCardWordId(flashCardWordId);
        flashCardWordRequest.setImage(image);

        FlashCardWordEntity flashCardWord = flashCardWordService.saveFlashCardWord(flashCardWordRequest);

        return FlashCardWordMapper.INSTANCE.toFlashCardWordResponse(flashCardWord);
    }

    @GetMapping("/searchByWord")
    @DefaultMessage("Show list successfully")
    public List<String> searchFlashCardByWord(@RequestParam(value = "query") String query) {

        return flashCardWordService.searchByFlashCardWord(query);
    }
}
