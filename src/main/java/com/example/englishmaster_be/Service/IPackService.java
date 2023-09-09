package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Model.Pack;

import java.util.List;

public interface IPackService {
    boolean createPack(Pack pack);
    boolean checkPack(Pack pack);

    List<Pack> getAllPack();
}
