package com.example.englishmaster_be.domain.topic.dto.request;

import com.example.englishmaster_be.common.dto.request.FilterRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Sort;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TopicFilterRequest extends FilterRequest {

    UUID packId;

    String search;

    String sortBy;

    String type;

    Sort.Direction sortDirection;

}
