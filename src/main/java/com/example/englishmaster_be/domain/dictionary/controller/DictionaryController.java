package com.example.englishmaster_be.domain.dictionary.controller;



import com.example.englishmaster_be.domain.dictionary.dto.res.DictionarySuggestionResponse;
import com.example.englishmaster_be.domain.dictionary.service.IDictionaryService;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name = "Dictionary")
@RestController
@RequestMapping("/dictionary")
public class DictionaryController {

    private final IDictionaryService dictionaryService;

    public DictionaryController(IDictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    @GetMapping("/search/{word}")
    public JsonNode searchWords(@PathVariable("word") String word) {

        return dictionaryService.searchWords(word);
    }


    @GetMapping("/suggest/{word}")
    public DictionarySuggestionResponse getSuggestions(@PathVariable("word") String word) throws IOException {

        return dictionaryService.getSuggestions(word);
    }


    @GetMapping("/image/{word}")
    public JsonNode getImage(@PathVariable("word") String word) {
        return dictionaryService.getImage(word);
    }

}
