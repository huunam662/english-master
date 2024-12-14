package com.example.englishmaster_be.service;

import com.example.englishmaster_be.model.request.Pack.PackRequest;
import com.example.englishmaster_be.entity.PackEntity;
import com.example.englishmaster_be.model.response.PackResponse;

import java.util.List;
import java.util.UUID;

public interface IPackService {

    PackEntity createPack(PackRequest packRequest);

    PackEntity checkPack(String packName);

    PackEntity getPackById(UUID packId);

    PackEntity getPackByName(String packName);

    List<PackEntity> getListPack();

}
