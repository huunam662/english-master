package com.example.englishmaster_be.domain.topic.dto.request;

import com.example.englishmaster_be.common.constant.sort.TopicSortBy;
import com.example.englishmaster_be.shared.dto.request.FilterRequest;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Sort;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@ParameterObject
public class TopicFilterRequest extends FilterRequest {

    @NotNull(message = "Pack id is required.")
    UUID packId;

    String search = "";

    Boolean enabled = true;

    TopicSortBy sortBy = TopicSortBy.DEFAULT;

    Sort.Direction sortDirection = Sort.Direction.ASC;

}
