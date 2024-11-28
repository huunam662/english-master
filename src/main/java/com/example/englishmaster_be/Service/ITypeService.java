package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.DTO.type.CreateTypeDTO;
import com.example.englishmaster_be.Model.Response.TypeResponse;

import java.util.List;
import java.util.UUID;

public interface ITypeService {
    List<TypeResponse> getAllTypes();

    TypeResponse getTypeById(UUID id);

    TypeResponse createType(CreateTypeDTO createTypeDTO);

    void deleteTypeById(UUID id);


}
