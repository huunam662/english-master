package com.example.englishmaster_be.domain.dictionary.controller;



<<<<<<< HEAD
import com.example.englishmaster_be.domain.dictionary.dto.res.DictionarySuggestionResponse;
=======
import com.example.englishmaster_be.domain.dictionary.dto.response.DictionarySuggestionResponse;
>>>>>>> 197ed81940903ab14a285d25c6aed6f94b8e649c
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
<<<<<<< HEAD
    public DictionarySuggestionResponse getSuggestions(@PathVariable("word") String word) throws IOException {
=======
    public DictionarySuggestionResponse getSuggestions(@PathVariable("word") String word) {
>>>>>>> 197ed81940903ab14a285d25c6aed6f94b8e649c

        return dictionaryService.getSuggestions(word);
    }


    @GetMapping("/image/{word}")
    public JsonNode getImage(@PathVariable("word") String word) {
        return dictionaryService.getImage(word);
    }

}
