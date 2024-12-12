package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Model.Request.Type.TypeRequest;
import com.example.englishmaster_be.Model.Response.TypeResponse;
import com.example.englishmaster_be.entity.TypeEntity;

import java.util.List;
import java.util.UUID;

public interface ITypeService {

    List<TypeEntity> getAllTypes();

    TypeEntity getTypeById(UUID id);

    TypeEntity saveType(TypeRequest typeRequest);

    void deleteTypeById(UUID id);

}
