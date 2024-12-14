package com.example.englishmaster_be.value;


import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Getter
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DictionaryValue {


    @Value("${dictionary.api}")
    String dictionaryApi;

    @Value("${unsplash.api.key}")
    String unsplashApiKey;

    @Value("${dictionary.file.path}")
    String fileName;

}
