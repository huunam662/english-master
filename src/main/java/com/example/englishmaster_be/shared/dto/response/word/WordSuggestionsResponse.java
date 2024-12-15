package com.example.englishmaster_be.shared.dto.response.word;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WordSuggestionsResponse {

    String[] suggestions;

}
