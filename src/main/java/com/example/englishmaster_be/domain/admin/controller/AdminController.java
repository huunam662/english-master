package com.example.englishmaster_be.domain.admin.controller;

import com.example.englishmaster_be.common.dto.response.FilterResponse;
import com.example.englishmaster_be.domain.admin.service.IAdminService;
import com.example.englishmaster_be.domain.user.dto.response.UserRes;
import com.example.englishmaster_be.domain.user.dto.request.UserFilterReq;
import com.example.englishmaster_be.domain.user.model.UserEntity;
import com.example.englishmaster_be.domain.user.mapper.UserMapper;
import com.example.englishmaster_be.domain.admin.dto.response.CountMockTestTopicResponse;
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
    public FilterResponse<?> getAllUser(
            @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(value = "sortBy", defaultValue = "updateAt") String sortBy,
            @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction sortDirection,
            @RequestParam(value = "enable", defaultValue = "true") Boolean enable
    ) {

        UserFilterReq userFilterRequest = UserFilterReq.builder()
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
    public void deleteUser(@PathVariable("userId") UUID userId) {

        adminService.deleteUser(userId);
    }

    @GetMapping(value = "/countMockTestOfTopic")
    @PreAuthorize("hasRole('ADMIN')")
    public List<CountMockTestTopicResponse> countMockTestOfTopic(
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "pack") UUID packId
    ) {

        return adminService.getCountMockTestOfTopic(date, packId);
    }

    // API để tìm kiếm người dùng lâu ngày chưa đăng nhập
    @GetMapping("/users/inactive")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserRes> getInactiveUsers() {

        List<UserEntity> inactiveUsers = adminService.getUsersNotLoggedInLast10Days();

        return UserMapper.INSTANCE.toUserResponseList(inactiveUsers);
    }

    @GetMapping("/inactive-notify")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserRes> notifyInactiveUsers(@RequestParam int days){

        List<UserEntity> notifyInactiveUsers = adminService.findUsersInactiveForDaysAndNotify(days);

        return UserMapper.INSTANCE.toUserResponseList(notifyInactiveUsers);
    }

    @PostMapping("/notifyInactiveUsers")
    @PreAuthorize("hasRole('ADMIN')")
    public void notifyInactiveUsers() {

        adminService.notifyInactiveUsers();
    }
}
