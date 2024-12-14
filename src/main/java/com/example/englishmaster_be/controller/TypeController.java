package com.example.englishmaster_be.controller;

import com.example.englishmaster_be.common.dto.response.FilterResponse;
import com.example.englishmaster_be.common.constaint.sort.SortByTypeFieldsEnum;
import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.mapper.TypeMapper;
import com.example.englishmaster_be.model.request.Type.TypeFilterRequest;
import com.example.englishmaster_be.model.request.Type.TypeRequest;
import com.example.englishmaster_be.model.response.TypeResponse;
import com.example.englishmaster_be.service.ITypeService;
import com.example.englishmaster_be.entity.TypeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Type")
@RestController
@RequestMapping("/type")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TypeController {

    ITypeService typeService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAllTypes")
    @DefaultMessage("List Type List successfully")
    public FilterResponse<?> getAllTypes(
            @Schema(description = "Trang")
            @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
            @Schema(description = "Kích thước của trang")
            @RequestParam(value = "pageSize", defaultValue = "8") @Min(1) @Max(100) int pageSize,
            @Schema(description = "Tìm kiếm")
            @RequestParam(value = "search", defaultValue = "") String search,
            @Schema(description = "Sắp xếp theo trường", allowableValues = {"None", "TypeName", "NameSlug"})
            @RequestParam(value = "sortBy", defaultValue = "None") SortByTypeFieldsEnum sortBy,
            @Schema(description = "Tùy chọn tăng giảm", allowableValues = {"ASC", "DESC"})
            @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction direction
    ) {

        TypeFilterRequest filterRequest = TypeFilterRequest.builder()
                .page(page)
                .pageSize(pageSize)
                .search(search)
                .sortBy(sortBy)
                .direction(direction)
                .build();

        return typeService.getTypeList(filterRequest);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getType/{id}")
    @DefaultMessage("Get Type successfully")
    public TypeResponse getType(@PathVariable("id") UUID id) {

        TypeEntity typeEntity = typeService.getTypeById(id);

        return TypeMapper.INSTANCE.toTypeResponse(typeEntity);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{typeId}/updateType")
    @DefaultMessage("Create Type successfully")
    public TypeResponse updateType(@PathVariable("typeId") UUID typeId, @RequestBody TypeRequest typeRequest) {

        typeRequest.setTypeId(typeId);

        TypeEntity typeEntity = typeService.saveType(typeRequest);

        return TypeMapper.INSTANCE.toTypeResponse(typeEntity);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/createType")
    @DefaultMessage("Create Type successfully")
    public TypeResponse createType(@RequestBody TypeRequest typeRequest) {

        TypeEntity typeEntity = typeService.saveType(typeRequest);

        return TypeMapper.INSTANCE.toTypeResponse(typeEntity);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    @DefaultMessage("Delete Type successfully")
    public void deleteType(@PathVariable("id") UUID id) {

        typeService.deleteTypeById(id);
    }
}

