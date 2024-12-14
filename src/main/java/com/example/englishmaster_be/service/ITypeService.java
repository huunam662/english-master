package com.example.englishmaster_be.service;

import com.example.englishmaster_be.common.dto.response.FilterResponse;
import com.example.englishmaster_be.model.request.Type.TypeFilterRequest;
import com.example.englishmaster_be.model.request.Type.TypeRequest;
import com.example.englishmaster_be.entity.TypeEntity;

import java.util.UUID;

public interface ITypeService {

    FilterResponse<?> getTypeList(TypeFilterRequest filterRequest);

    TypeEntity getTypeById(UUID id);

    TypeEntity saveType(TypeRequest typeRequest);

    void deleteTypeById(UUID id);

}
