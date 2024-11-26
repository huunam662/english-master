package com.example.englishmaster_be.service;

import com.example.englishmaster_be.model.Pack;

import java.util.List;
import java.util.UUID;

public interface IPackService {
    boolean createPack(Pack pack);
    boolean checkPack(Pack pack);

    Pack findPackById(UUID packId);

    List<Pack> getAllPack();
}
