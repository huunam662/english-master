package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Common.dto.response.FilterResponse;
import com.example.englishmaster_be.Model.Request.Type.TypeFilterRequest;
import com.example.englishmaster_be.Model.Request.Type.TypeRequest;
import com.example.englishmaster_be.Model.Response.TypeResponse;
import com.example.englishmaster_be.entity.TypeEntity;

import java.util.List;
import java.util.UUID;

public interface ITypeService {

    FilterResponse<?> getTypeList(TypeFilterRequest filterRequest);

    TypeEntity getTypeById(UUID id);

    TypeEntity saveType(TypeRequest typeRequest);

    void deleteTypeById(UUID id);

}
