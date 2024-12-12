package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Configuration.global.annotation.MessageResponse;
import com.example.englishmaster_be.Mapper.TypeMapper;
import com.example.englishmaster_be.Model.Request.Type.TypeRequest;
import com.example.englishmaster_be.Model.Response.TypeResponse;
import com.example.englishmaster_be.Service.ITypeService;
import com.example.englishmaster_be.entity.TypeEntity;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    @MessageResponse("List Type List successfully")
    public List<TypeResponse> getAllTypes() {

        List<TypeEntity> typeEntityList = typeService.getAllTypes();

        return TypeMapper.INSTANCE.toTypeResponseList(typeEntityList);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getType/{id}")
    @MessageResponse("Get Type successfully")
    public TypeResponse getType(@PathVariable("id") UUID id) {

        TypeEntity typeEntity = typeService.getTypeById(id);

        return TypeMapper.INSTANCE.toTypeResponse(typeEntity);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{typeId}/updateType")
    @MessageResponse("Create Type successfully")
    public TypeResponse updateType(@PathVariable("typeId") UUID typeId, @RequestBody TypeRequest typeRequest) {

        typeRequest.setTypeId(typeId);

        TypeEntity typeEntity = typeService.saveType(typeRequest);

        return TypeMapper.INSTANCE.toTypeResponse(typeEntity);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/createType")
    @MessageResponse("Create Type successfully")
    public TypeResponse createType(@RequestBody TypeRequest typeRequest) {

        TypeEntity typeEntity = typeService.saveType(typeRequest);

        return TypeMapper.INSTANCE.toTypeResponse(typeEntity);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    @MessageResponse("Delete Type successfully")
    public void deleteType(@PathVariable("id") UUID id) {

        typeService.deleteTypeById(id);
    }
}

