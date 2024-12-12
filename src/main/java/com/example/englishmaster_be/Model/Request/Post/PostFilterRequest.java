package com.example.englishmaster_be.Model.Request.Post;

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
public class PostFilterRequest extends FilterRequest {

    int size;

    String sortBy;

    Sort.Direction sortDirection;

}
