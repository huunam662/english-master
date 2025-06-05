package com.example.englishmaster_be.domain.pack.service;

import com.example.englishmaster_be.domain.pack.dto.request.PackRequest;
import com.example.englishmaster_be.model.pack.PackEntity;

import java.util.List;
import java.util.UUID;

public interface IPackService {

    PackEntity createPack(PackRequest packRequest);

    PackEntity checkPack(String packName);

    PackEntity getPackById(UUID packId);

    PackEntity getPackByName(String packName);

    List<PackEntity> getListPack();

    List<PackEntity> getListPackByPackTypeId(UUID packTypeId);
}
