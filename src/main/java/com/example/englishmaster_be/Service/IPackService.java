package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Model.Pack;

import java.util.List;
import java.util.UUID;

public interface IPackService {
    boolean createPack(Pack pack);
    boolean checkPack(Pack pack);

    Pack findPackById(UUID packId);

    List<Pack> getAllPack();
}
