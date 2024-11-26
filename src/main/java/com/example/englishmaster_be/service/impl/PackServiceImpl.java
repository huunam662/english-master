package com.example.englishmaster_be.service.impl;

import com.example.englishmaster_be.model.*;
import com.example.englishmaster_be.repository.*;
import com.example.englishmaster_be.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PackServiceImpl implements IPackService {

    @Autowired
    private PackRepository packRepository;
    @Override
    public boolean createPack(Pack pack) {
        boolean check = checkPack(pack);
        if(check){
            packRepository.save(pack);
            return true;
        }
        return false;
    }

    @Override
    public boolean checkPack(Pack pack) {
        List<Pack> packList = packRepository.findAll();
        for(Pack packCheck : packList){
            if (packCheck.getPackName().equalsIgnoreCase(pack.getPackName())){
                return false;
            }
        }
        return true;
    }

    @Override
    public Pack findPackById(UUID packId) {
        return packRepository.findByPackId(packId)
                .orElseThrow(() -> new IllegalArgumentException("Pack not found with ID: " + packId));
    }

    @Override
    public List<Pack> getAllPack() {
        return packRepository.findAll();
    }
}
