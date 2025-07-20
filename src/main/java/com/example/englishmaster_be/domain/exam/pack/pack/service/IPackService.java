package com.example.englishmaster_be.domain.exam.pack.pack.service;

import com.example.englishmaster_be.common.dto.req.PageOptionsReq;
import com.example.englishmaster_be.domain.exam.pack.pack.dto.view.IPackKeyView;
import com.example.englishmaster_be.domain.exam.pack.pack.dto.req.PackReq;
import com.example.englishmaster_be.domain.exam.pack.pack.dto.view.IPackPageView;
import com.example.englishmaster_be.domain.exam.pack.pack.model.PackEntity;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface IPackService {

    PackEntity createPack(PackReq packRequest);

    PackEntity checkPack(String packName);

    PackEntity getPackById(UUID packId);

    PackEntity getPackByName(String packName);

    List<PackEntity> getListPack();

    List<PackEntity> getListPackByPackTypeId(UUID packTypeId);

    IPackKeyView getPackKeyProjection(String packName);

    PackEntity savePack(PackEntity pack);

    Page<IPackPageView> getPagePack(PageOptionsReq optionsReq);
}
