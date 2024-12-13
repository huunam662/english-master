package com.example.englishmaster_be.Model.Request.Feedback;

import com.example.englishmaster_be.Common.dto.request.FilterRequest;
import com.example.englishmaster_be.Common.enums.sort.SortByFeedbackFieldsEnum;
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

        String search;

        SortByFeedbackFieldsEnum sortBy;

        Sort.Direction direction;

        Boolean isEnable;

}
