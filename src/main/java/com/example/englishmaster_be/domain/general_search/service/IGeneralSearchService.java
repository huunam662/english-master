package com.example.englishmaster_be.domain.general_search.service;

import com.example.englishmaster_be.domain.general_search.dto.response.GeneralSearchAllResponse;

public interface IGeneralSearchService {

    GeneralSearchAllResponse searchAll(String keyword);

}

