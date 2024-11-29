package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Helper.EnglishWordDictionary;
import com.example.englishmaster_be.Model.Response.DictionarySuggestionResponse;
import com.example.englishmaster_be.Service.IDictionaryService;
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
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DictionaryServiceImpl implements IDictionaryService {

    CloseableHttpClient httpClient;

    ObjectMapper objectMapper;

    EnglishWordDictionary englishWordDictionary;

    @SneakyThrows
    @Override
    public Object searchWords(String word) {

        String encodedWord = URLEncoder.encode(word, StandardCharsets.UTF_8)
                .replace("+", "%20");

        String apiUrl = String.format("%s/%s", DictionaryValue.dictionaryApi, encodedWord);

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
    public Object getImage(String word) {

        String apiUrl = "https://api.unsplash.com/search/photos?query=" + word;

        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", "Client-ID " + DictionaryValue.unsplashApiKey);

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

        if (results == null || results.isEmpty()) return null;

        JsonNode firstResult = results.get(0);

        return firstResult.get("urls");
    }
}
