package com.example.englishmaster_be.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.function.Function;

public class PageUtil {

    public static Pageable unSortPageable(Pageable pageable){
        if(pageable == null) return null;
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
    }

    public static Pageable reBuildSortPageable(Pageable pageable, Function<String, String> propertyMapper) {
        if(pageable.getSort().isEmpty()) return pageable;
        Sort newSort = Sort.by(pageable.getSort().stream()
                .map(order -> {
                    String newProperty = propertyMapper.apply(order.getProperty());
                    return new Sort.Order(
                            order.getDirection(),
                            newProperty,
                            order.getNullHandling()
                    );
                })
                .toList());

        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                newSort
        );
    }

}
