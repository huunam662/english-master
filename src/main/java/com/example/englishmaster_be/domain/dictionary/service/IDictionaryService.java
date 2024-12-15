package com.example.englishmaster_be.domain.dictionary.service;

import com.example.englishmaster_be.domain.dictionary.dto.response.DictionarySuggestionResponse;
import com.fasterxml.jackson.databind.JsonNode;

public interface IDictionaryService {

    JsonNode searchWords(String word);

    JsonNode getImage(String word);

    DictionarySuggestionResponse getSuggestions(String word);

}
