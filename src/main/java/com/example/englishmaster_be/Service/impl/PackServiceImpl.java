package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public List<Pack> getAllPack() {
        return packRepository.findAll();
    }
}
