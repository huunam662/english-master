package com.example.englishmaster_be.value;


import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
public class DictionaryValue {

    @Value("${dictionary.api}")
    String dictionaryApi;

    @Value("${unsplash.api.key}")
    String unsplashApiKey;

}
