package com.example.englishmaster_be.domain.type.service;

import com.example.englishmaster_be.common.dto.response.FilterResponse;
import com.example.englishmaster_be.domain.type.dto.request.TypeFilterRequest;
import com.example.englishmaster_be.domain.type.dto.request.TypeRequest;
import com.example.englishmaster_be.model.type.TypeEntity;

import java.util.UUID;

public interface ITypeService {

    FilterResponse<?> getTypeList(TypeFilterRequest filterRequest);

    TypeEntity getTypeById(UUID id);

    TypeEntity saveType(TypeRequest typeRequest);

    void deleteTypeById(UUID id);

}
