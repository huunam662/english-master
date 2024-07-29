package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.DTO.Type.CreateTypeDTO;
import com.example.englishmaster_be.Model.Response.TypeResponse;
import com.example.englishmaster_be.Model.ResponseModel;
import com.example.englishmaster_be.Service.ITypeService;
import com.example.englishmaster_be.Service.impl.TypeServiceImpl;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/type")
@PreAuthorize("hasRole('ADMIN')")
public class TypeController {
    @Autowired
    ITypeService typeService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAllTypes")
    public ResponseEntity<ResponseModel> getAllTypes() {
        ResponseModel responseModel = new ResponseModel();
        List<TypeResponse> typeList = typeService.getAllTypes();

        if (typeList == null || typeList.isEmpty()) {
            responseModel.setMessage("No types found");
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }

        JSONObject responseObject = new JSONObject();
        responseObject.put("typeList", typeList);

        responseModel.setMessage("List type successful");
        responseModel.setResponseData(responseObject);
        responseModel.setStatus("success");

        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getType/{id}")
    public ResponseEntity<ResponseModel> getType(@PathVariable("id") UUID id) {
        ResponseModel responseModel = new ResponseModel();
        TypeResponse typeResponse = typeService.getTypeById(id);
        if (typeResponse == null) {
            responseModel.setMessage("No type found");
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
        JSONObject responseObject = new JSONObject();
        responseObject.put("type", typeResponse);

        responseModel.setMessage("Type successful");
        responseModel.setResponseData(responseObject);
        responseModel.setStatus("success");

        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/createType")
    public ResponseEntity<ResponseModel> createType(@RequestBody CreateTypeDTO createTypeDTO) {
        ResponseModel responseModel = new ResponseModel();
        TypeResponse typeResponse = typeService.createType(createTypeDTO);
        if (typeResponse == null) {
            responseModel.setMessage("Create type failed");
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
        JSONObject responseObject = new JSONObject();
        responseObject.put("type", typeResponse);

        responseModel.setMessage("Create type successful");
        responseModel.setResponseData(responseObject);
        responseModel.setStatus("success");

        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseModel> deleteType(@PathVariable("id") UUID id) {
        ResponseModel responseModel = new ResponseModel();
        TypeResponse typeResponse = typeService.getTypeById(id);
        if (typeResponse == null) {
            responseModel.setMessage("No type found");
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
        typeService.deleteTypeById(id);
        responseModel.setMessage("Delete account of user successful");
        responseModel.setStatus("success");

        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }
}

