package com.example.englishmaster_be.DTO.User;


import com.example.englishmaster_be.Common.dto.request.FilterRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserFilterRequest extends FilterRequest {

    int size;

    String sortBy;

    Sort.Direction sortDirection;

    Boolean enable;

    int inactiveUser;

    LocalDate lastLoginDate;
}
