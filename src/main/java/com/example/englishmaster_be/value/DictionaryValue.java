package com.example.englishmaster_be.value;


import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class DictionaryValue {

    @Value("${dictionary.api}")
    private String dictionaryApi;

    @Value("${unsplash.api.key}")
    private String unsplashApiKey;

    @Value("${dictionary.file.path}")
    private String fileName;

}
