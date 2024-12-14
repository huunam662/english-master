package com.example.englishmaster_be.Value;


import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DictionaryValue {

    @Value("${dictionary.api}")
    String dictionaryApi;

    @Value("${unsplash.api.key}")
    String unsplashApiKey;

}
