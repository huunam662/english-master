package com.example.englishmaster_be.Controller;


import com.example.englishmaster_be.Configuration.global.annotation.MessageResponse;
import com.example.englishmaster_be.Model.Response.DictionarySuggestionResponse;
import com.example.englishmaster_be.Service.IDictionaryService;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Dictionary")
@RestController
@RequestMapping("/dictionary")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DictionaryController {

    IDictionaryService dictionaryService;


    @GetMapping("/search/{word}")
    @MessageResponse("Search successfully")
    public JsonNode searchWords(@PathVariable String word) {

        return dictionaryService.searchWords(word);
    }


    @GetMapping("/suggest/{word}")
    @MessageResponse("Show word successfully")
    public DictionarySuggestionResponse getSuggestions(@PathVariable String word) {

        return dictionaryService.getSuggestions(word);
    }


    @GetMapping("/image/{word}")
    @MessageResponse("show image to word successfully")
    public JsonNode getImage(@PathVariable String word) {

        return dictionaryService.getImage(word);
    }

}
