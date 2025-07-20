package com.example.englishmaster_be.domain.exam.pack.type.service;

import com.example.englishmaster_be.advice.exception.ApplicationException;
import com.example.englishmaster_be.common.dto.req.PageOptionsReq;
import com.example.englishmaster_be.domain.exam.pack.type.dto.req.CreatePackTypeReq;
import com.example.englishmaster_be.domain.exam.pack.type.dto.req.UpdatePackTypeReq;
import com.example.englishmaster_be.domain.exam.pack.type.dto.res.PackTypeKeyRes;
import com.example.englishmaster_be.domain.exam.pack.type.dto.view.IPackTypePageView;
import com.example.englishmaster_be.domain.exam.pack.type.repository.PackTypeDslRepository;
import com.example.englishmaster_be.domain.exam.pack.type.repository.PackTypeRepository;
import com.example.englishmaster_be.domain.exam.pack.type.mapper.PackTypeMapper;
import com.example.englishmaster_be.domain.exam.pack.type.model.PackTypeEntity;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import java.util.List;
import java.util.UUID;

@Service
public class PackTypeService implements IPackTypeService{

    private final PackTypeRepository packTypeRepository;
    private final PackTypeDslRepository packTypeDslRepository;

    @Lazy
    public PackTypeService(PackTypeRepository packTypeRepository, PackTypeDslRepository packTypeDslRepository) {
        this.packTypeRepository = packTypeRepository;
        this.packTypeDslRepository = packTypeDslRepository;
    }

    @Override
    public List<PackTypeEntity> getAllPackTypes() {

        return packTypeRepository.findAll();
    }

    @Override
    public PackTypeEntity getPackTypeById(UUID id) {

        return packTypeRepository.findById(id)
                .orElseThrow(
                        () -> new ApplicationException(HttpStatus.NOT_FOUND, "Pack type not found with id: " + id)
                );
    }

    @Override
    public Page<IPackTypePageView> getPagePackType(PageOptionsReq optionsReq) {
        return packTypeDslRepository.findPagePackType(optionsReq);
    }


    @Transactional
    @Override
    public PackTypeKeyRes createPackType(CreatePackTypeReq createPackTypeRequest) {

        isExistedByPackTypeName(createPackTypeRequest.getName(), true);

        PackTypeEntity packType = PackTypeMapper.INSTANCE.toPackTypeEntity(createPackTypeRequest);

        packType = packTypeRepository.save(packType);

        return PackTypeMapper.INSTANCE.toPackTypeKeyResponse(packType);
    }

    @Transactional
    @Override
    public PackTypeKeyRes updatePackType(UpdatePackTypeReq updatePackTypeRequest) {

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
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Pack type already exists");

        return isExistedByName;
    }

    @Override
    public UUID getPackTypeIdByName(String packTypeName) {
        return packTypeRepository.findPackTypeIdByName(packTypeName);
    }

    @Transactional
    @Override
    public PackTypeEntity savePackType(PackTypeEntity packTypeEntity) {
        Assert.notNull(packTypeEntity, "Pack type to save is required.");
        return packTypeRepository.save(packTypeEntity);
    }
}
