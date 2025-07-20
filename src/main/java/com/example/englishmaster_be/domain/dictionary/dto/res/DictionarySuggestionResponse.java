package com.example.englishmaster_be.domain.dictionary.dto.res;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
public class DictionarySuggestionResponse {

    Set<String> newSet;

    public DictionarySuggestionResponse(Set<String> newSet) {
        this.newSet = newSet;
    }
}
