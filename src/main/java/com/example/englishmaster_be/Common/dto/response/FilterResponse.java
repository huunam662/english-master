package com.example.englishmaster_be.Common.dto.response;

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

    Integer pageNumber = 0;

    Integer pageSize = 0;

    Long offset = 0L;

    Long totalPages = 0L;

    Boolean hasPreviousPage = Boolean.FALSE;

    Boolean hasNextPage = Boolean.FALSE;

    List<T> content = null;

    public void withPreviousAndNextPage() {

        this.hasPreviousPage = pageNumber > 1;
        this.hasNextPage = pageNumber < totalPages;
    }

}
