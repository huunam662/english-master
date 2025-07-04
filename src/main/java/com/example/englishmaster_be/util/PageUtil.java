package com.example.englishmaster_be.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PageUtil {

    public static Pageable unSortPageable(Pageable pageable){
        if(pageable == null) return null;
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
    }
}
