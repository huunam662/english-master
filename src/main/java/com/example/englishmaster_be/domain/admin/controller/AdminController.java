package com.example.englishmaster_be.domain.admin.controller;

import com.example.englishmaster_be.shared.dto.response.FilterResponse;
import com.example.englishmaster_be.domain.admin.service.IAdminService;
import com.example.englishmaster_be.domain.user.dto.response.UserResponse;
import com.example.englishmaster_be.domain.user.dto.request.UserFilterRequest;
import com.example.englishmaster_be.model.user.UserEntity;
import com.example.englishmaster_be.mapper.UserMapper;
import com.example.englishmaster_be.domain.admin.dto.response.CountMockTestTopicResponse;
import com.example.englishmaster_be.common.annotation.DefaultMessage;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@Tag(name = "Admin")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminController {


    IAdminService adminService;


    @GetMapping(value = "/getAllUser")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("List User successfully")
    public FilterResponse<?> getAllUser(
            @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(value = "sortBy", defaultValue = "updateAt") String sortBy,
            @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction sortDirection,
            @RequestParam(value = "enable", defaultValue = "true") Boolean enable
    ) {

        UserFilterRequest userFilterRequest = UserFilterRequest.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .enable(enable)
                .build();

        return adminService.getAllUser(userFilterRequest);

    }

    @PatchMapping(value = "/{userId:.+}/enableUser")
    @PreAuthorize("hasRole('ADMIN')")
    public void enableUser(@PathVariable("userId") UUID userId, @RequestParam("enable") Boolean enable) {

        adminService.enableUser(userId, enable);
    }

    @DeleteMapping(value = "/{userId:.+}/deleteUser")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Delete account of User successfully")
    public void deleteUser(@PathVariable("userId") UUID userId) {

        adminService.deleteUser(userId);
    }

    @GetMapping(value = "/countMockTestOfTopic")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("List Topic and count mock test successfully")
    public List<CountMockTestTopicResponse> countMockTestOfTopic(
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "pack") UUID packId
    ) {

        return adminService.getCountMockTestOfTopic(date, packId);
    }

    // API để tìm kiếm người dùng lâu ngày chưa đăng nhập
    @GetMapping("/users/inactive")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("List inactive users successfully")
    public List<UserResponse> getInactiveUsers() {

        List<UserEntity> inactiveUsers = adminService.getUsersNotLoggedInLast10Days();

        return UserMapper.INSTANCE.toUserResponseList(inactiveUsers);
    }

    @GetMapping("/inactive-notify")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Notify inactive users successfully")
    public List<UserResponse> notifyInactiveUsers(@RequestParam int days){

        List<UserEntity> notifyInactiveUsers = adminService.findUsersInactiveForDaysAndNotify(days);

        return UserMapper.INSTANCE.toUserResponseList(notifyInactiveUsers);
    }

    @PostMapping("/notifyInactiveUsers")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Notify inactive users successfully")
    public void notifyInactiveUsers() {

        adminService.notifyInactiveUsers();
    }
}
