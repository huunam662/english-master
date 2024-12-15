package com.example.englishmaster_be.domain.user.dto.request;


import com.example.englishmaster_be.common.dto.request.FilterRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Sort;

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
