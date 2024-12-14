package com.example.englishmaster_be.controller;

import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.model.response.GeneralSearchAllResponse;
import com.example.englishmaster_be.service.GeneralSearchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "General search")
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GeneralSearchController{

    GeneralSearchService generalSearchService;


    @GetMapping("/searchAll")
    @DefaultMessage("Search successfully")
    public GeneralSearchAllResponse searchAll(@RequestParam(value = "keyword", defaultValue = "") String keyword){

       return generalSearchService.searchAll(keyword);
    }
}
