package com.example.englishmaster_be.DTO.Feedback;

import com.example.englishmaster_be.Common.DTO.Request.FilterRequest;
import com.example.englishmaster_be.Common.Enums.SortByFeedbackFieldsEnum;
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
public class FeedbackFilterRequest extends FilterRequest {

        int size;

        SortByFeedbackFieldsEnum sortBy;

        Sort.Direction direction;

        Boolean isEnable;

}
