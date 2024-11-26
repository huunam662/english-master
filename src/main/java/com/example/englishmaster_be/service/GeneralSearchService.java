package com.example.englishmaster_be.service;

import java.util.List;
import java.util.Map;

public interface GeneralSearchService {
    Map<String, List<Object>> searchAll(String keyword);
}

