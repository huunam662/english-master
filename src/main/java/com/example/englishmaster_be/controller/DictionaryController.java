package com.example.englishmaster_be.controller;


import com.example.englishmaster_be.common.response.ExceptionResponseModel;
import com.example.englishmaster_be.helper.EnglishWordDictionary;
import com.example.englishmaster_be.common.response.ResponseModel;
import com.fasterxml.jackson.databind.*;
import org.apache.http.Header;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dictionary")
public class DictionaryController {
    //    private final OxfordConfig oxfordConfig;
    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    @Value("${dictionary.api}")
    private String dictionaryApi;

    @Value("${unsplash.api.key}")
    private String unsplashApiKey;


    private final EnglishWordDictionary englishWordDictionary;

    public DictionaryController(RestTemplate restTemplate, EnglishWordDictionary englishWordDictionary) {
        this.httpClient = HttpClients.createDefault();
        this.objectMapper = new ObjectMapper();
        this.restTemplate = restTemplate;
        this.englishWordDictionary = englishWordDictionary;
    }


    @GetMapping("/search/{word}")
    public ResponseEntity<ResponseModel> searchWords(@PathVariable String word) throws IOException {
        ResponseModel responseModel = new ResponseModel();
        String encodedWord = URLEncoder.encode(word, "UTF-8")
                .replace("+", "%20");
        String apiUrl = String.format("%s/%s", dictionaryApi, encodedWord);

        HttpGet httpGet = new HttpGet(apiUrl);
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                String responseBody = EntityUtils.toString(response.getEntity());

                JsonNode jsonResponse = objectMapper.readTree(responseBody);

                responseModel.setMessage("Search successful");
                responseModel.setResponseData(jsonResponse);

                return ResponseEntity.status(HttpStatus.OK).body(responseModel);
            } else {
                ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();

                exceptionResponseModel.setResponseData("Don't search");
                exceptionResponseModel.setStatus(HttpStatus.BAD_REQUEST);
                exceptionResponseModel.setViolations(String.valueOf(statusCode));

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponseModel);
            }
        } catch (IOException e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();

            exceptionResponseModel.setMessage("Don't search: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }

    }

    @GetMapping("/suggest/{word}")
    public ResponseEntity<ResponseModel> getSuggestions(@PathVariable String word) {
        ResponseModel responseModel = new ResponseModel();
        try {
            Set<String> wordSet = englishWordDictionary.getWordSet();

            Set<String> newSet = wordSet.stream()
                    .filter(wordSince -> wordSince.startsWith(word))
                    .limit(7)
                    .collect(Collectors.toSet());

            responseModel.setResponseData(newSet);
            responseModel.setMessage("Show word successful");
            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Don't show word: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }


    @GetMapping("/image/{word}")
    public ResponseEntity<ResponseModel> getImage(@PathVariable String word) {
        ResponseModel responseModel = new ResponseModel();
        try {
            String apiUrl = "https://api.unsplash.com/search/photos?query=" + word;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Client-ID " + unsplashApiKey);

            Header[] httpHeaders = headers
                    .entrySet()
                    .stream()
                    .flatMap(entry -> entry.getValue().stream().map(value -> new BasicHeader(entry.getKey(), value)))
                    .toArray(Header[]::new);

            try (CloseableHttpClient httpClient = HttpClients.createDefault()){
                    HttpGet httpGet = new HttpGet(apiUrl);
                    httpGet.setHeaders(httpHeaders);

                try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == 200) {
                        String responseBody = EntityUtils.toString(response.getEntity());

                        JsonNode jsonResponse = objectMapper.readTree(responseBody);

                        JsonNode results = jsonResponse.get("results");
                        if (results != null && results.size() > 0) {
                            JsonNode firstResult = results.get(0);
                            JsonNode urls = firstResult.get("urls");
                            if (urls != null) {
                                responseModel.setResponseData(urls);
                            }
                        }

                        responseModel.setMessage("show image to word successful");
                    } else {
                        responseModel.setMessage("Don't search image to word");
                        responseModel.setViolations(String.valueOf(statusCode));
                    }
                }
            }
            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Don't show image: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

}
