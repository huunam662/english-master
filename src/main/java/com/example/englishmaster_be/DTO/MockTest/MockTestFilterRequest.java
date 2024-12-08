package com.example.englishmaster_be.DTO.MockTest;


import com.example.englishmaster_be.Common.dto.request.FilterRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestParam;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MockTestFilterRequest extends FilterRequest {

    int size;

    String sortBy;

    Sort.Direction sortDirection;

}
