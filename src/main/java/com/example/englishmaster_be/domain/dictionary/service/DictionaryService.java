package com.example.englishmaster_be.domain.dictionary.service;

import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.common.constant.error.Error;
import com.example.englishmaster_be.helper.EnglishWordDictionaryHelper;
import com.example.englishmaster_be.domain.dictionary.dto.response.DictionarySuggestionResponse;
import com.example.englishmaster_be.value.DictionaryValue;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.message.BasicClassicHttpRequest;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DictionaryService implements IDictionaryService {

    ObjectMapper objectMapper;

    DictionaryValue dictionaryValue;

    EnglishWordDictionaryHelper englishWordDictionaryHelper;

    RestTemplate restTemplate;



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
    public DictionarySuggestionResponse getSuggestions(String word) {

        Set<String> wordSet = englishWordDictionaryHelper.getWordSet();

        Set<String> newSet = wordSet.stream()
                .filter(wordSince -> wordSince.startsWith(word))
                .limit(7)
                .collect(Collectors.toSet());

        return DictionarySuggestionResponse.builder()
                .newSet(newSet)
                .build();
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
            throw new ErrorHolder(Error.RESOURCE_NOT_FOUND, "The urls is empty or not found");

        JsonNode firstResult = results.get(0);

        return firstResult.get("urls");
    }
}
