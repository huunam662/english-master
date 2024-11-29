package com.example.englishmaster_be.value;


import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class DictionaryValue {


    public static String dictionaryApi;

    public static String unsplashApiKey;

    @Autowired
    void setDictionaryApi(
            @Value("${dictionary.api}")
            String dictionaryApi
    ) {
        DictionaryValue.dictionaryApi = dictionaryApi;
    }


    @Autowired
    void setUnsplashApiKey(
            @Value("${unsplash.api.key}")
            String unsplashApiKey
    ) {
        DictionaryValue.unsplashApiKey = unsplashApiKey;
    }

}
