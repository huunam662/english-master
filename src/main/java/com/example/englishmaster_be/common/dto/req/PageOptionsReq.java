package com.example.englishmaster_be.common.dto.req;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@Data
@ParameterObject
public class PageOptionsReq {

    @Min(value = 1, message = "Page value required min is 1.")
    @Parameter(example = "1", description = "Number of page.")
    private int page = 1;
    @Min(value = 1, message = "Page size required min is 1.")
    @Parameter(example = "1", description = "Size of page.")
    private int size = 1;
    private List<String> sortBy;
    Sort.Direction direction;
    private String filter;
    private String having;

    public Pageable getPageable(){
        if(sortBy == null || sortBy.isEmpty() || direction == null)
            return PageRequest.of(page - 1, size);
        return PageRequest.of(page - 1, size, Sort.by(direction, sortBy.toArray(String[]::new)));
    }
}
