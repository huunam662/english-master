package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Model.Response.DictionarySuggestionResponse;
import com.fasterxml.jackson.databind.JsonNode;

public interface IDictionaryService {

    JsonNode searchWords(String word);

    JsonNode getImage(String word);

    DictionarySuggestionResponse getSuggestions(String word);

}
