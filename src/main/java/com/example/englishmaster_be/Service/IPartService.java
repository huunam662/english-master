package com.example.englishmaster_be.Service;

import com.example.englishmaster_be.Model.Part;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

public interface IPartService {
    boolean createPart(Part part);

    void updatePart(Part part);

    List<Part> getAllPart();

    Part getPartToId(UUID partId);

    boolean checkPart(Part part);

    boolean checkFilePart(Part part);
    void deletePart(Part part);

}
