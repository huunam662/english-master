package com.example.englishmaster_be.domain.pack.service;

import com.example.englishmaster_be.domain.pack.dto.IPackKeyProjection;
import com.example.englishmaster_be.domain.pack.dto.request.PackOptionsFilterRequest;
import com.example.englishmaster_be.domain.pack.dto.request.PackRequest;
import com.example.englishmaster_be.domain.pack.model.PackEntity;
import com.example.englishmaster_be.domain.pack_type.dto.projection.IPackTypeKeyProjection;
import com.example.englishmaster_be.domain.pack_type.service.IPackTypeService;
import com.example.englishmaster_be.shared.dto.response.FilterResponse;

import java.util.List;
import java.util.UUID;

public interface IPackService {

    PackEntity createPack(PackRequest packRequest);

    PackEntity checkPack(String packName);

    PackEntity getPackById(UUID packId);

    PackEntity getPackByName(String packName);

    List<PackEntity> getListPack();

    List<PackEntity> getListPackByPackTypeId(UUID packTypeId);

    IPackKeyProjection getPackKeyProjection(String packName);

    PackEntity savePack(PackEntity pack);

    FilterResponse<?> filterPack(UUID packTypeId, PackOptionsFilterRequest request);
}
