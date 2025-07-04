package com.example.englishmaster_be.domain.mock_test.dto.request;


import com.example.englishmaster_be.common.dto.request.FilterRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Sort;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MockTestFilterRequest extends FilterRequest {

    Integer size;

    String sortBy;

    Sort.Direction sortDirection;

}
