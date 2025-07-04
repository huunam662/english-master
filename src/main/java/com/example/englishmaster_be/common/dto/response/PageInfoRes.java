package com.example.englishmaster_be.common.dto.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageInfoRes<T> {

    int pageNumber;
    int pageSize;
    int contentLength;
    long offset;
    long totalPages;
    boolean hasPreviousPage;
    boolean hasNextPage;
    List<T> content;

    public PageInfoRes(Page<T> pageObject){
        Pageable pageable = pageObject.getPageable();
        this.pageNumber = pageable.getPageNumber() + 1;
        this.pageSize = pageable.getPageSize();
        this.offset = pageable.getOffset();
        this.totalPages = pageObject.getTotalPages();
        this.content = pageObject.getContent();
        this.contentLength = pageObject.getNumberOfElements();
        this.hasNextPage = pageObject.hasNext();
        this.hasPreviousPage = pageObject.hasPrevious();
    }
}
