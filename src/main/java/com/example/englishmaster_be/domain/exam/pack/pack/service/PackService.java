package com.example.englishmaster_be.domain.exam.pack.pack.service;

import com.example.englishmaster_be.advice.exception.ApplicationException;
import com.example.englishmaster_be.common.dto.req.PageOptionsReq;
import com.example.englishmaster_be.domain.exam.pack.pack.dto.view.IPackKeyView;
import com.example.englishmaster_be.domain.exam.pack.pack.model.PackEntity;
import com.example.englishmaster_be.domain.exam.pack.pack.dto.view.IPackPageView;
import com.example.englishmaster_be.domain.exam.pack.pack.dto.req.PackReq;
import com.example.englishmaster_be.domain.exam.pack.pack.repository.PackDslRepository;
import com.example.englishmaster_be.domain.exam.pack.pack.repository.PackRepository;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;


@Service
public class PackService implements IPackService {

    private final PackRepository packRepository;
    private final PackDslRepository packDslRepository;

    public PackService(PackRepository packRepository, PackDslRepository packDslRepository) {
        this.packRepository = packRepository;
        this.packDslRepository = packDslRepository;
    }

    @Override
    public PackEntity createPack(PackReq packRequest) {

        PackEntity isExistedPack = checkPack(packRequest.getPackName());

        if(isExistedPack != null)
            throw new ApplicationException(HttpStatus.CONFLICT, "Create pack fail: The pack name is already exist");

        PackEntity pack = new PackEntity();
        pack.setPackName(packRequest.getPackName());

        return packRepository.save(pack);
    }

    @Override
    public PackEntity checkPack(String packName) {

        List<PackEntity> packList = packRepository.findAll();

        return packList.stream()
                .filter(
                        pack -> pack.getPackName().equalsIgnoreCase(packName)
                ).findFirst().orElse(null);
    }

    @Override
    public PackEntity getPackById(UUID packId) {

        return packRepository.findByPackId(packId)
                .orElseThrow(
                        () -> new ApplicationException(HttpStatus.NOT_FOUND, "Pack not found")
                );
    }

    @Override
    public PackEntity getPackByName(String packName) {
        return packRepository.findByPackName(packName).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "Pack not found with name: " + packName)
        );
    }

    @Override
    public List<PackEntity> getListPack() {

        return packRepository.findAll();
    }

    @Override
    public List<PackEntity> getListPackByPackTypeId(UUID packTypeId) {

        return packRepository.getAllByPackTypeId(packTypeId);
    }

    @Override
    public IPackKeyView getPackKeyProjection(String packName) {
        return packRepository.findPackIdByName(packName);
    }

    @Transactional
    @Override
    public PackEntity savePack(PackEntity pack) {
        return packRepository.save(pack);
    }

    @Override
    public Page<IPackPageView> getPagePack(PageOptionsReq optionsReq) {
        return packDslRepository.findPagePack(optionsReq);
    }
}
