package com.example.englishmaster_be.domain.pack_type.service;

import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.common.constant.error.Error;
import com.example.englishmaster_be.domain.pack_type.dto.request.CreatePackTypeRequest;
import com.example.englishmaster_be.domain.pack_type.dto.request.PackTypeFilterRequest;
import com.example.englishmaster_be.domain.pack_type.dto.request.UpdatePackTypeRequest;
import com.example.englishmaster_be.domain.pack_type.dto.response.PackTypeKeyResponse;
import com.example.englishmaster_be.domain.pack_type.dto.response.PackTypeResponse;
import com.example.englishmaster_be.mapper.PackTypeMapper;
import com.example.englishmaster_be.model.pack_type.PackTypeEntity;
import com.example.englishmaster_be.model.pack_type.PackTypeQueryFactory;
import com.example.englishmaster_be.model.pack_type.PackTypeRepository;
import com.example.englishmaster_be.shared.dto.request.FilterRequest;
import com.example.englishmaster_be.shared.dto.response.FilterResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PackTypeService implements IPackTypeService{

    PackTypeRepository packTypeRepository;

    PackTypeQueryFactory queryFactory;

    @Override
    public List<PackTypeEntity> getAllPackTypes() {

        return packTypeRepository.findAll();
    }

    @Override
    public PackTypeEntity getPackTypeById(UUID id) {

        return packTypeRepository.findById(id)
                .orElseThrow(
                        () -> new ErrorHolder(Error.RESOURCE_NOT_FOUND, "Pack type not found with id: " + id, false)
                );
    }

    @Override
    public FilterResponse<PackTypeResponse> filterPackTypes(PackTypeFilterRequest filterRequest) {

        return queryFactory.filterToResponse(filterRequest);
    }


    @Transactional
    @Override
    public PackTypeKeyResponse createPackType(CreatePackTypeRequest createPackTypeRequest) {

        isExistedByPackTypeName(createPackTypeRequest.getName(), true);

        PackTypeEntity packType = PackTypeMapper.INSTANCE.toPackTypeEntity(createPackTypeRequest);

        packType = packTypeRepository.save(packType);

        return PackTypeMapper.INSTANCE.toPackTypeKeyResponse(packType);
    }

    @Transactional
    @Override
    public PackTypeKeyResponse updatePackType(UpdatePackTypeRequest updatePackTypeRequest) {

        PackTypeEntity packType = getPackTypeById(updatePackTypeRequest.getPackTypeId());

        isExistedByPackTypeName(updatePackTypeRequest.getName(), true);

        PackTypeMapper.INSTANCE.flowToPackTypeEntity(packType, updatePackTypeRequest);

        packType = packTypeRepository.save(packType);

        return PackTypeMapper.INSTANCE.toPackTypeKeyResponse(packType);
    }

    @Transactional
    @Override
    public void deletePackTypeById(UUID id) {

        PackTypeEntity packTypeEntity = getPackTypeById(id);

        packTypeRepository.delete(packTypeEntity);
    }

    @Override
    public Boolean isExistedByPackTypeName(String packTypeName, Boolean throwable) {

        Boolean isExistedByName = packTypeRepository.existsByName(packTypeName);

        if(isExistedByName && throwable)
            throw new ErrorHolder(Error.BAD_REQUEST, "Pack type already exists");

        return isExistedByName;
    }
}
