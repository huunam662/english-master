package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Configuration.global.annotation.MessageResponse;
import com.example.englishmaster_be.Model.Response.ExceptionResponseModel;
import com.example.englishmaster_be.DTO.Type.SaveTypeDTO;
import com.example.englishmaster_be.Model.Response.TypeResponse;
import com.example.englishmaster_be.Model.Response.ResponseModel;
import com.example.englishmaster_be.Model.Type;
import com.example.englishmaster_be.Service.ITypeService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Type")
@RestController
@RequestMapping("/api/type")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TypeController {

    ITypeService typeService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAllTypes")
    @MessageResponse("List Type successfully")
    public List<TypeResponse> getAllTypes() {

        return typeService.getAllTypes();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getType/{id}")
    @MessageResponse("Type successfully")
    public TypeResponse getType(@PathVariable("id") UUID id) {

        return typeService.getTypeById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/createType")
    @MessageResponse("Create Type successfully")
    public TypeResponse createType(@RequestBody SaveTypeDTO createTypeDTO) {

        return typeService.createType(createTypeDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    @MessageResponse("Delete Type successfully")
    public void deleteType(@PathVariable("id") UUID id) {

        typeService.deleteTypeById(id);
    }
}

