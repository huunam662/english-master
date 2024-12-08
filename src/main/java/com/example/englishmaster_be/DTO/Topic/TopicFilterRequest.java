package com.example.englishmaster_be.DTO.Topic;

import com.example.englishmaster_be.Common.dto.request.FilterRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import net.bytebuddy.implementation.bind.annotation.Super;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TopicFilterRequest extends FilterRequest {

    UUID packId;

    int page;

    int size;

    String sortBy;

    String type;

    Sort.Direction sortDirection;

}
