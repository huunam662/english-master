package com.example.englishmaster_be.domain.dictionary.util;

import com.example.englishmaster_be.domain.mock_test.evaluator_writing.util.SpringApplicationContext;
import com.example.englishmaster_be.value.DictionaryValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class DictionaryUtil {

    public static Set<String> loadWordList() throws IOException {
        DictionaryValue dictionaryValue = SpringApplicationContext.getBean(DictionaryValue.class);
        try(Stream<String> words = Files.lines(Paths.get(new ClassPathResource(dictionaryValue.getFileName()).getURI()))){
            return words.collect(Collectors.toSet());
        }
        catch (Exception e){
            return Collections.emptySet();
        }
    }

}
