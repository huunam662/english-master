package com.example.englishmaster_be.domain.dictionary.controller;



import com.example.englishmaster_be.domain.dictionary.dto.response.DictionarySuggestionResponse;
import com.example.englishmaster_be.domain.dictionary.service.IDictionaryService;
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
    public JsonNode searchWords(@PathVariable("word") String word) {

        return dictionaryService.searchWords(word);
    }


    @GetMapping("/suggest/{word}")
    public DictionarySuggestionResponse getSuggestions(@PathVariable("word") String word) {

        return dictionaryService.getSuggestions(word);
    }


    @GetMapping("/image/{word}")
    public JsonNode getImage(@PathVariable("word") String word) {
        return dictionaryService.getImage(word);
    }

}
