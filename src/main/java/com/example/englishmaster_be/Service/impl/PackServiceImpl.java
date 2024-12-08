package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.DTO.Pack.PackDTO;
import com.example.englishmaster_be.Exception.Response.BadRequestException;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.PackResponse;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PackServiceImpl implements IPackService {

    PackRepository packRepository;

    IUserService userService;

    @Override
    public PackResponse createPack(PackDTO packDTO) {

        Pack isExistedPack = checkPack(packDTO.getPackName());

        if(isExistedPack != null)
            throw new BadRequestException("Create pack fail: The pack name is already exist");

        User user = userService.currentUser();

        Pack pack = Pack.builder()
                .packName(packDTO.getPackName())
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .userCreate(user)
                .userUpdate(user)
                .build();

        pack = packRepository.save(pack);

        return new PackResponse(pack);
    }

    @Override
    public Pack checkPack(String packName) {

        List<Pack> packList = packRepository.findAll();

        return packList.stream()
                .filter(
                        pack -> pack.getPackName().equalsIgnoreCase(packName)
                ).findFirst().orElse(null);
    }

    @Override
    public Pack findPackById(UUID packId) {

        return packRepository.findByPackId(packId)
                .orElseThrow(
                        () -> new IllegalArgumentException("Pack not found with ID: " + packId)
                );
    }

    @Override
    public List<PackResponse> getListPack() {

        List<Pack> packList = packRepository.findAll();

        return packList.stream().map(PackResponse::new).toList();
    }
}
