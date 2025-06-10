package com.example.englishmaster_be.domain.flash_card.dto.request;

import com.example.englishmaster_be.common.constant.sort.FlashCardSortBy;
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
public class FlashCardFilterRequest extends FilterRequest {

    @Parameter(description = "Search by title or description of flash card.")
    String search;

    @Parameter(description = "Sort by field.")
    FlashCardSortBy sortBy = FlashCardSortBy.DEFAULT;

    @Parameter(description = "Sort direction.")
    Sort.Direction direction = Sort.Direction.DESC;

}
