package com.example.englishmaster_be.domain.user.user.controller;

import com.example.englishmaster_be.domain.user.user.dto.req.UserChangeProfileReq;
import com.example.englishmaster_be.domain.user.user.dto.res.UserProfileRes;
import com.example.englishmaster_be.domain.user.user.model.UserEntity;
import com.example.englishmaster_be.domain.user.user.mapper.UserMapper;
import com.example.englishmaster_be.domain.user.user.service.IUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


@Tag(name = "User")
@RestController
@RequestMapping("/user")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/information")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public UserProfileRes informationUser() {

        UserEntity currentUser = userService.currentUser();

        return UserMapper.INSTANCE.toInformationUserResponse(currentUser);
    }

    @PatchMapping(value = "/changeProfile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public UserProfileRes changeProfile(
            @RequestBody UserChangeProfileReq changeProfileRequest
    ) {

        UserEntity user = userService.changeProfile(changeProfileRequest);

        return UserMapper.INSTANCE.toInformationUserResponse(user);
    }

    @GetMapping("/{email:.+}/check-email")
    public Map<String, Boolean> checkEmail(@PathVariable("email") String email){

        return Map.of("isExisting", userService.isExistingEmail(email));
    }

    @PostMapping(value = "/import/excel", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasRole('ADMIN')")
    public void importAllUserFromExcel(@RequestPart("file") MultipartFile file) {
        userService.saveAllUsersToExcel(file);
    }

}
