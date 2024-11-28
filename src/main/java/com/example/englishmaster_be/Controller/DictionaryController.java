package com.example.englishmaster_be.Controller;


import com.example.englishmaster_be.Configuration.global.annotation.MessageResponse;
import com.example.englishmaster_be.Model.Response.DictionarySuggestionResponse;
import com.example.englishmaster_be.Model.Response.ExceptionResponseModel;
import com.example.englishmaster_be.Helper.EnglishWordDictionary;
import com.example.englishmaster_be.Model.Response.ResponseModel;
import com.example.englishmaster_be.Service.IDictionaryService;
import com.fasterxml.jackson.databind.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.http.Header;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dictionary")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DictionaryController {

    IDictionaryService dictionaryService;


    @GetMapping("/search/{word}")
    @MessageResponse("Search successfully")
    public Object searchWords(@PathVariable String word) {

        return dictionaryService.searchWords(word);
    }

    @GetMapping("/suggest/{word}")
    @MessageResponse("Show word successfully")
    public DictionarySuggestionResponse getSuggestions(@PathVariable String word) {

        return dictionaryService.getSuggestions(word);
    }


    @GetMapping("/image/{word}")
    @MessageResponse("show image to word successfully")
    public Object getImage(@PathVariable String word) {

        return dictionaryService.getImage(word);
    }

}
