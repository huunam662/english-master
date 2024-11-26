package com.example.englishmaster_be.Helper;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

@Component
public class EnglishWordDictionary {
    //@Value("${dictionary.file.path}")
    private String dictionaryFilePath = "english.txt";

    private Set<String> wordSet = new HashSet<>();

    @PostConstruct
    public void loadWordList() throws IOException {
        try (Stream<String> stream = Files.lines(Paths.get(new ClassPathResource(dictionaryFilePath).getURI()))) {
            stream.forEach(wordSet::add);
        }
    }

    public Set<String> getWordSet() {
        return wordSet;
    }
}
