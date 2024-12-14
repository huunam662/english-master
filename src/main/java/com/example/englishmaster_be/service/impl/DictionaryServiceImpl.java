package com.example.englishmaster_be.service.impl;

import com.example.englishmaster_be.helper.EnglishWordDictionaryHelper;
import com.example.englishmaster_be.model.response.DictionarySuggestionResponse;
import com.example.englishmaster_be.service.IDictionaryService;
import com.example.englishmaster_be.value.DictionaryValue;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DictionaryServiceImpl implements IDictionaryService {

    CloseableHttpClient httpClient;

    ObjectMapper objectMapper;

    DictionaryValue dictionaryValue;

    EnglishWordDictionaryHelper englishWordDictionary;



    @SneakyThrows
    @Override
    public JsonNode searchWords(String word) {

        String encodedWord = URLEncoder.encode(word, StandardCharsets.UTF_8)
                .replace("+", "%20");

        String apiUrl = String.format("%s/%s", dictionaryValue.getDictionaryApi(), encodedWord);

        HttpGet httpGet = new HttpGet(apiUrl);

        CloseableHttpResponse response = httpClient.execute(httpGet);

        String responseBody = EntityUtils.toString(response.getEntity());

        return objectMapper.readTree(responseBody);
    }

    @Override
    public DictionarySuggestionResponse getSuggestions(String word) {

        Set<String> wordSet = englishWordDictionary.getWordSet();

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

        Header[] httpHeaders = headers
                .entrySet()
                .stream()
                .flatMap(entry -> entry.getValue().stream().map(value -> new BasicHeader(entry.getKey(), value)))
                .toArray(Header[]::new);

        HttpGet httpGet = new HttpGet(apiUrl);

        httpGet.setHeaders(httpHeaders);

        CloseableHttpResponse response = httpClient.execute(httpGet);

        String responseBody = EntityUtils.toString(response.getEntity());

        JsonNode jsonResponse = objectMapper.readTree(responseBody);

        JsonNode results = jsonResponse.get("results");

        if (results == null || results.isEmpty())
            throw new NoSuchElementException("The urls is empty or not found");

        JsonNode firstResult = results.get(0);

        return firstResult.get("urls");
    }
}
