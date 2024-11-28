package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Exception.CustomException;
import com.example.englishmaster_be.Exception.Error;
import com.example.englishmaster_be.Model.Part;
import com.example.englishmaster_be.Model.Response.PartResponse;
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
        if (check) {
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
        return partRepository.findByPartId(partId)
                .orElseThrow(() -> new CustomException(Error.PART_NOT_FOUND));
    }

    @Override
    public Part getPartToName(String partName) {
        List<Part> listPart = partRepository.findAll();
        for (Part part : listPart) {
            if (part.getPartName().substring(0, 6).equalsIgnoreCase(partName)) {
                return part;
            }
        }
        return null;
    }

    @Override
    public boolean checkPart(Part part) {
        List<Part> partList = partRepository.findAll();
        for (Part partRes : partList) {
            System.out.println(new PartResponse(partRes).getPartName());
        }
        for (Part partCheck : partList) {
            if (partCheck.getPartName().equalsIgnoreCase(part.getPartName())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean checkFilePart(Part part) {
        if (part.getContentType() == null)
            return false;
        return true;
    }

    @Override
    public void deletePart(Part part) {
        partRepository.delete(part);
    }
}
