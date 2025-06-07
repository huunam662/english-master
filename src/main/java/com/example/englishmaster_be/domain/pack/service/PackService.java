package com.example.englishmaster_be.domain.pack.service;

import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.common.constant.error.Error;
import com.example.englishmaster_be.domain.user.service.IUserService;
import com.example.englishmaster_be.domain.pack.dto.request.PackRequest;
import com.example.englishmaster_be.domain.pack.repository.jpa.PackRepository;
import com.example.englishmaster_be.domain.pack.model.PackEntity;
import com.example.englishmaster_be.domain.user.model.UserEntity;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PackService implements IPackService {

    PackRepository packRepository;

    IUserService userService;

    @Override
    public PackEntity createPack(PackRequest packRequest) {

        PackEntity isExistedPack = checkPack(packRequest.getPackName());

        if(isExistedPack != null)
            throw new ErrorHolder(Error.CONFLICT, "Create pack fail: The pack name is already exist");

        UserEntity user = userService.currentUser();

        PackEntity pack = PackEntity.builder()
                .packName(packRequest.getPackName())
                .userCreate(user)
                .userUpdate(user)
                .build();

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
                        () -> new ErrorHolder(Error.RESOURCE_NOT_FOUND, "PackEntity not found", false)
                );
    }

    @Override
    public PackEntity getPackByName(String packName) {
        return packRepository.findByPackName(packName).orElseThrow(
                () -> new ErrorHolder(Error.RESOURCE_NOT_FOUND, "PackEntity not found with name: " + packName, false)
        );
    }

    @Override
    public List<PackEntity> getListPack() {

        return packRepository.findAll();
    }

    @Override
    public List<PackEntity> getListPackByPackTypeId(UUID packTypeId) {

        Assert.notNull(packTypeId, "Pack type id is required.");

        return packRepository.getAllByPackTypeId(packTypeId);
    }
}
