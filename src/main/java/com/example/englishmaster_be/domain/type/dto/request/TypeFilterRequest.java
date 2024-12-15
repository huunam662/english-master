package com.example.englishmaster_be.domain.type.dto.request;

import com.example.englishmaster_be.common.dto.request.FilterRequest;
import com.example.englishmaster_be.common.constant.sort.SortByTypeFieldsEnum;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class TypeFilterRequest extends FilterRequest {

    @Schema(description = "Tìm kiếm theo tên")
    String search;

    @Schema(description = "Sắp xếp theo trường", defaultValue = "None")
    SortByTypeFieldsEnum sortBy;

    @Schema(description = "Tùy chọn tăng giảm", defaultValue = "DESC")
    Sort.Direction direction;

}
