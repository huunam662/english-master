package com.example.englishmaster_be.shared.dto.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageResponse <T> {

    int page;
    int pageSize;
    List<T> elements;
    int elementsCount;
    boolean hasNextPage;
    boolean hasPreviousPage;

    public PageResponse(Page<T> pageObject){
        Pageable pageable = pageObject.getPageable();
        this.page = pageable.getPageNumber();
        this.pageSize = pageable.getPageSize();
        this.elements = pageObject.getContent();
        this.elementsCount = this.elements.size();
        this.hasNextPage = pageObject.hasNext();
        this.hasPreviousPage = pageObject.hasPrevious();
    }
}
