package com.example.englishmaster_be.domain.dictionary.service;

import com.example.englishmaster_be.advice.exception.ApplicationException;
import com.example.englishmaster_be.domain.dictionary.dto.res.DictionarySuggestionResponse;
import com.example.englishmaster_be.domain.dictionary.util.DictionaryUtil;
import com.example.englishmaster_be.value.DictionaryValue;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class DictionaryService implements IDictionaryService {

    private final ObjectMapper objectMapper;
    private final DictionaryValue dictionaryValue;
    private final RestTemplate restTemplate;

    @Lazy
    public DictionaryService(ObjectMapper objectMapper, DictionaryValue dictionaryValue, RestTemplate restTemplate) {
        this.objectMapper = objectMapper;
        this.dictionaryValue = dictionaryValue;
        this.restTemplate = restTemplate;
    }

    @SneakyThrows
    @Override
    public JsonNode searchWords(String word) {

        String encodedWord = URLEncoder.encode(word, StandardCharsets.UTF_8)
                .replace("+", "%20");

        String apiUrl = String.format("%s/%s", dictionaryValue.getDictionaryApi(), encodedWord);

        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

        return objectMapper.readTree(response.getBody());
    }

    @Override
    public DictionarySuggestionResponse getSuggestions(String word) throws IOException {

        Set<String> wordSet = DictionaryUtil.loadWordList();

        Set<String> newSet = wordSet.stream()
                .filter(wordSince -> wordSince.startsWith(word))
                .limit(7)
                .collect(Collectors.toSet());

        return new DictionarySuggestionResponse(newSet);
    }

    @SneakyThrows
    @Override
    public JsonNode getImage(String word) {

        String apiUrl = "https://api.unsplash.com/search/photos?query=" + word;

        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", "Client-ID " + dictionaryValue.getUnsplashApiKey());

        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                apiUrl, HttpMethod.GET, httpEntity, String.class
        );

        JsonNode jsonResponse = objectMapper.readTree( response.getBody());

        JsonNode results = jsonResponse.get("results");

        if (results == null || results.isEmpty())
            throw new ApplicationException(HttpStatus.NOT_FOUND, "The urls is empty or not found");

        JsonNode firstResult = results.get(0);

        return firstResult.get("urls");
    }
}
