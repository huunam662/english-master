package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Model.Response.DictionarySuggestionResponse;

public interface IDictionaryService {

    Object searchWords(String word);

    Object getImage(String word);

    DictionarySuggestionResponse getSuggestions(String word);

}
