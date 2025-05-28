package com.example.englishmaster_be.domain.pack_type.dto.request;

import com.example.englishmaster_be.common.constant.sort.PackTypeSortBy;
import com.example.englishmaster_be.shared.dto.request.FilterRequest;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Sort;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@ParameterObject
public class PackTypeFilterRequest extends FilterRequest {

    @Parameter(description = "Search by name.")
    String search = "";

    @Parameter(description = "Sort by field.", name = "sort-by")
    PackTypeSortBy sortBy = PackTypeSortBy.DEFAULT;

    @Parameter(description = "Sort direction.")
    Sort.Direction direction = Sort.Direction.DESC;

}
