package com.example.englishmaster_be.domain.dictionary.service;

import com.example.englishmaster_be.domain.dictionary.dto.res.DictionarySuggestionResponse;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public interface IDictionaryService {

    JsonNode searchWords(String word);

    JsonNode getImage(String word);

    DictionarySuggestionResponse getSuggestions(String word) throws IOException;

}
