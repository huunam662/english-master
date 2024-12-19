package com.example.englishmaster_be.domain.user.contorller;

import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.common.dto.response.FilterResponse;
import com.example.englishmaster_be.domain.user.dto.request.*;
import com.example.englishmaster_be.domain.user.dto.response.UserProfileResponse;
import com.example.englishmaster_be.model.user.UserEntity;
import com.example.englishmaster_be.mapper.UserMapper;
import com.example.englishmaster_be.domain.user.service.IUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@Tag(name = "User")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    IUserService userService;


    @GetMapping("/information")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Information user successfully")
    public UserProfileResponse informationUser() {

        UserEntity currentUser = userService.currentUser();

        return UserMapper.INSTANCE.toInformationUserResponse(currentUser);
    }

    @PatchMapping(value = "/changeProfile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Change profile user successfully")
    public UserProfileResponse changeProfile(
            @ModelAttribute("profileUser") UserChangeProfileRequest changeProfileRequest
    ) {

        UserEntity user = userService.changeProfile(changeProfileRequest);

        return UserMapper.INSTANCE.toInformationUserResponse(user);
    }


    @GetMapping(value = "/listExamResultsUser")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("Show list exam result successfully")
    public FilterResponse<?> getExamResultsUser(
            @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
            @RequestParam(value = "size", defaultValue = "5") @Min(1) @Max(100) int size,
            @RequestParam(value = "sortBy", defaultValue = "updateAt") String sortBy,
            @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction sortDirection
    ) {

        UserFilterRequest userFilterRequest = UserFilterRequest.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        return userService.getExamResultsUser(userFilterRequest);
    }

}
