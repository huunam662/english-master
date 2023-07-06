package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.DTO.Part.*;
import com.example.englishmaster_be.Model.CustomUserDetails;
import com.example.englishmaster_be.Model.Part;
import com.example.englishmaster_be.Model.ResponseModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/part")
public class PartController {
    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> createPart(@ModelAttribute CreatePartDTO createpartDTO){
        ResponseModel responseModel = new ResponseModel();

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            Part part = new Part();

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        }
        catch (Exception e){responseModel.setMessage("Create part fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);}
    }
}
