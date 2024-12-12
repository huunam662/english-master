package com.example.englishmaster_be.Model.Request.MockTest;


import com.example.englishmaster_be.Common.dto.request.FilterRequest;
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
