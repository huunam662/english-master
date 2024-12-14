package com.example.englishmaster_be.service;

import com.example.englishmaster_be.model.response.DictionarySuggestionResponse;
import com.fasterxml.jackson.databind.JsonNode;

public interface IDictionaryService {

    JsonNode searchWords(String word);

    JsonNode getImage(String word);

    DictionarySuggestionResponse getSuggestions(String word);

}
