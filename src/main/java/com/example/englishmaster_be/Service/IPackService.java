package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.DTO.Pack.PackDTO;
import com.example.englishmaster_be.Model.Pack;
import com.example.englishmaster_be.Model.Response.PackResponse;

import java.util.List;
import java.util.UUID;

public interface IPackService {

    PackResponse createPack(PackDTO packDTO);

    Pack checkPack(String packName);

    Pack findPackById(UUID packId);

    List<PackResponse> getListPack();

}
