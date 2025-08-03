package com.example.englishmaster_be.domain.general_search.controller;


import com.example.englishmaster_be.domain.general_search.dto.response.GeneralSearchAllResponse;
import com.example.englishmaster_be.domain.general_search.service.IGeneralSearchService;
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

    IGeneralSearchService generalSearchService;


    @GetMapping("/searchAll")
    public GeneralSearchAllResponse searchAll(@RequestParam(value = "keyword", defaultValue = "") String keyword){

       return generalSearchService.searchAll(keyword);
    }
}
