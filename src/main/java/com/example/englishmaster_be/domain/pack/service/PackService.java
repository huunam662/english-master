package com.example.englishmaster_be.domain.pack.service;

import com.example.englishmaster_be.domain.user.service.IUserService;
import com.example.englishmaster_be.domain.pack.dto.request.PackRequest;
import com.example.englishmaster_be.exception.template.BadRequestException;
import com.example.englishmaster_be.model.pack.PackRepository;
import com.example.englishmaster_be.model.pack.PackEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PackService implements IPackService {

    PackRepository packRepository;

    IUserService userService;

    @Override
    public PackEntity createPack(PackRequest packRequest) {

        PackEntity isExistedPack = checkPack(packRequest.getPackName());

        if(isExistedPack != null)
            throw new BadRequestException("Create pack fail: The pack name is already exist");

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
                        () -> new BadRequestException("PackEntity not found")
                );
    }

    @Override
    public PackEntity getPackByName(String packName) {
        return packRepository.findByPackName(packName).orElseThrow(
                () -> new BadRequestException("PackEntity not found with name: " + packName)
        );
    }

    @Override
    public List<PackEntity> getListPack() {

        return packRepository.findAll();
    }
}
