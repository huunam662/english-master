package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Model.Response.ExceptionResponseModel;
import com.example.englishmaster_be.DTO.type.CreateTypeDTO;
import com.example.englishmaster_be.Model.Response.TypeResponse;
import com.example.englishmaster_be.Model.Response.ResponseModel;
import com.example.englishmaster_be.Service.ITypeService;
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
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("No types found");
            exceptionResponseModel.setStatus(HttpStatus.NOT_FOUND);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponseModel);
        }

        JSONObject responseObject = new JSONObject();
        responseObject.put("typeList", typeList);

        responseModel.setMessage("List type successful");
        responseModel.setResponseData(responseObject);

        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getType/{id}")
    public ResponseEntity<ResponseModel> getType(@PathVariable("id") UUID id) {
        ResponseModel responseModel = new ResponseModel();
        ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
        TypeResponse typeResponse = typeService.getTypeById(id);
        if (typeResponse == null) {
            exceptionResponseModel.setMessage("No type found");
            exceptionResponseModel.setStatus(HttpStatus.NOT_FOUND);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponseModel);
        }
        JSONObject responseObject = new JSONObject();
        responseObject.put("type", typeResponse);

        responseModel.setMessage("type successful");
        responseModel.setResponseData(responseObject);


        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/createType")
    public ResponseEntity<ResponseModel> createType(@RequestBody CreateTypeDTO createTypeDTO) {
        ResponseModel responseModel = new ResponseModel();
        TypeResponse typeResponse = typeService.createType(createTypeDTO);
        if (typeResponse == null) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Create type failed");
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
        JSONObject responseObject = new JSONObject();
        responseObject.put("type", typeResponse);

        responseModel.setMessage("Create type successful");
        responseModel.setResponseData(responseObject);


        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseModel> deleteType(@PathVariable("id") UUID id) {
        ResponseModel responseModel = new ResponseModel();
        TypeResponse typeResponse = typeService.getTypeById(id);
        if (typeResponse == null) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("No type found");
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
        typeService.deleteTypeById(id);
        responseModel.setMessage("Delete account of user successful");


        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }
}

