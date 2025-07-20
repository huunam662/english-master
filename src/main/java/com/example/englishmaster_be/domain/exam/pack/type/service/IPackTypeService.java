package com.example.englishmaster_be.domain.exam.pack.type.service;

import com.example.englishmaster_be.common.dto.req.PageOptionsReq;
import com.example.englishmaster_be.domain.exam.pack.type.dto.req.CreatePackTypeReq;
import com.example.englishmaster_be.domain.exam.pack.type.dto.req.UpdatePackTypeReq;
import com.example.englishmaster_be.domain.exam.pack.type.dto.res.PackTypeKeyRes;
import com.example.englishmaster_be.domain.exam.pack.type.dto.view.IPackTypePageView;
import com.example.englishmaster_be.domain.exam.pack.type.model.PackTypeEntity;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface IPackTypeService {

    PackTypeEntity getPackTypeById(UUID id);

    Page<IPackTypePageView> getPagePackType(PageOptionsReq optionsReq);

    List<PackTypeEntity> getAllPackTypes();

    PackTypeKeyRes createPackType(CreatePackTypeReq createPackTypeRequest);

    PackTypeKeyRes updatePackType(UpdatePackTypeReq updatePackTypeRequest);

    void deletePackTypeById(UUID id);

    Boolean isExistedByPackTypeName(String packTypeName, Boolean throwable);

    UUID getPackTypeIdByName(String packTypeName);

    PackTypeEntity savePackType(PackTypeEntity packTypeEntity);

}
