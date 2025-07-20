package com.example.englishmaster_be.common.dto.res;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Data
@NoArgsConstructor
public class PageInfoRes<T> {

    private int pageNumber;
    private int pageSize;
    private int contentLength;
    private long offset;
    private long totalPages;
    private boolean hasPreviousPage;
    private boolean hasNextPage;
    private List<T> content;

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
