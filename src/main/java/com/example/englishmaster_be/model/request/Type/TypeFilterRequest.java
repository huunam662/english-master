package com.example.englishmaster_be.model.request.Type;

import com.example.englishmaster_be.common.dto.request.FilterRequest;
import com.example.englishmaster_be.common.constaint.sort.SortByTypeFieldsEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Sort;

import javax.enterprise.inject.Default;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TypeFilterRequest extends FilterRequest {

    @Schema(description = "Tìm kiếm theo tên")
    String search;

    @Schema(description = "Sắp xếp theo trường", defaultValue = "None")
    SortByTypeFieldsEnum sortBy;

    @Schema(description = "Tùy chọn tăng giảm", defaultValue = "DESC")
    Sort.Direction direction;

}
