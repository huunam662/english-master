package com.example.englishmaster_be.Model.Request.News;


import com.example.englishmaster_be.Common.dto.request.FilterRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Sort;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewsFilterRequest extends FilterRequest {

    String search;

    String sortBy;

    Sort.Direction sortDirection;

    Boolean isEnable;

}
