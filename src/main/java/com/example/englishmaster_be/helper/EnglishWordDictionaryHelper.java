package com.example.englishmaster_be.helper;

import com.example.englishmaster_be.value.DictionaryValue;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

@Component
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class EnglishWordDictionaryHelper {

    DictionaryValue dictionaryValue;

    final Set<String> wordSet = new HashSet<>();

    @PostConstruct
    public void loadWordList() throws IOException {

        try(Stream<String> words = Files.lines(Paths.get(new ClassPathResource(dictionaryValue.getFileName()).getURI()))){
            words.forEach(
                    wordSet::add
            );
        }
    }

}
