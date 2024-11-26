package com.example.englishmaster_be.service;

import com.example.englishmaster_be.dto.type.CreateTypeDTO;
import com.example.englishmaster_be.model.response.TypeResponse;
import com.example.englishmaster_be.model.Type;

import java.util.List;
import java.util.UUID;

public interface ITypeService {
    List<TypeResponse> getAllTypes();

    TypeResponse getTypeById(UUID id);

    TypeResponse createType(CreateTypeDTO createTypeDTO);

    void deleteTypeById(UUID id);


}
