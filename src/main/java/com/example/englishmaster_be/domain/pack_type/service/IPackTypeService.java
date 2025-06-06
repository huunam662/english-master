package com.example.englishmaster_be.domain.pack_type.service;

import com.example.englishmaster_be.domain.pack_type.dto.request.CreatePackTypeRequest;
import com.example.englishmaster_be.domain.pack_type.dto.request.PackTypeFilterRequest;
import com.example.englishmaster_be.domain.pack_type.dto.request.UpdatePackTypeRequest;
import com.example.englishmaster_be.domain.pack_type.dto.response.PackTypeKeyResponse;
import com.example.englishmaster_be.domain.pack_type.dto.response.PackTypeResponse;
import com.example.englishmaster_be.model.pack_type.PackTypeEntity;
import com.example.englishmaster_be.shared.dto.response.FilterResponse;

import java.util.List;
import java.util.UUID;

public interface IPackTypeService {

    PackTypeEntity getPackTypeById(UUID id);

    FilterResponse<PackTypeResponse> filterPackTypes(PackTypeFilterRequest filterRequest);

    List<PackTypeEntity> getAllPackTypes();

    PackTypeKeyResponse createPackType(CreatePackTypeRequest createPackTypeRequest);

    PackTypeKeyResponse updatePackType(UpdatePackTypeRequest updatePackTypeRequest);

    void deletePackTypeById(UUID id);

    Boolean isExistedByPackTypeName(String packTypeName, Boolean throwable);

}
