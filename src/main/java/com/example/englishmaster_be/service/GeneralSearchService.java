package com.example.englishmaster_be.service;

import com.example.englishmaster_be.model.response.GeneralSearchAllResponse;

public interface GeneralSearchService {

    GeneralSearchAllResponse searchAll(String keyword);

}

