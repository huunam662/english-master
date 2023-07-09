package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Model.Part;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.IPartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PartSeviceImpl implements IPartService {
    @Autowired
    private PartRepository partRepository;
    @Override
    public boolean createPart(Part part) {
        boolean check = checkPart(part);
        if(check){
            partRepository.save(part);
            return true;
        }
        return false;
    }

    @Override
    public void updatePart(Part part) {
        partRepository.save(part);
    }

    @Override
    public List<Part> getAllPart() {
        return partRepository.findAll();
    }

    @Override
    public Part getPartToId(UUID partId) {
        Part part = partRepository.findByPartId(partId)
                .orElseThrow(() -> new IllegalArgumentException("Part not found with ID: " + partId));
        return part;
    }

    @Override
    public boolean checkPart(Part part) {
        List<Part> partList = partRepository.findAll();
        for(Part partCheck : partList){
            if (partCheck.getPartName().toLowerCase().equals(part.getPartName().toLowerCase())){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean checkFilePart(Part part) {
        if(part.getContentType() == null)
            return false;
        return true;
    }
}
