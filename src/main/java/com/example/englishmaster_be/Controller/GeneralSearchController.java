package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Service.GeneralSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class GeneralSearchController{
    private final GeneralSearchService generalSearchService;
    @GetMapping("/searchAll")
   public ResponseEntity<Map<String, List<Object>>>searchAll(@RequestParam("keyword")String keyword){
       Map<String, List<Object>> searchResult = generalSearchService.searchAll(keyword);
       return ResponseEntity.ok(searchResult);
   }

}
