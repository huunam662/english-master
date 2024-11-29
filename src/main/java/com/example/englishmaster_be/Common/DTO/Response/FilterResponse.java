package com.example.englishmaster_be.Common.DTO.Response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilterResponse<T> {

    int pageNumber = 0;

    int pageSize = 0;

    long offset = 0;

    long totalPages = 0;

    long totalElements = 0;

    boolean hasPreviousPage = false;

    boolean hasNextPage = false;

    List<T> content = null;

    public void withPreviousAndNextPage() {
        this.hasPreviousPage = pageNumber > 1;
        this.hasNextPage = pageNumber < totalPages;
    }

}
