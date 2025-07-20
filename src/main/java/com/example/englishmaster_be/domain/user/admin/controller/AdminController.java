package com.example.englishmaster_be.domain.user.admin.controller;

import com.example.englishmaster_be.common.dto.req.PageOptionsReq;
import com.example.englishmaster_be.common.dto.res.PageInfoRes;
import com.example.englishmaster_be.domain.user.admin.service.IAdminService;
import com.example.englishmaster_be.domain.user.user.dto.res.UserPageRes;
import com.example.englishmaster_be.domain.user.user.dto.res.UserRes;
import com.example.englishmaster_be.domain.user.user.dto.view.IUserPageView;
import com.example.englishmaster_be.domain.user.user.model.UserEntity;
import com.example.englishmaster_be.domain.user.user.mapper.UserMapper;
import com.example.englishmaster_be.domain.user.admin.dto.res.CountMockTestTopicRes;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@Tag(name = "Admin")
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final IAdminService adminService;

    public AdminController(IAdminService adminService) {
        this.adminService = adminService;
    }


    @GetMapping("/users/page")
    public PageInfoRes<UserPageRes> getPageUser(@ModelAttribute @Valid PageOptionsReq optionsReq){
        Page<IUserPageView> pageUserViews = adminService.getPageUser(optionsReq);
        List<UserPageRes> pageUserResList = UserMapper.INSTANCE.toPageUserResList(pageUserViews.getContent());
        Page<UserPageRes> pageUserRes = new PageImpl<>(pageUserResList, pageUserViews.getPageable(), pageUserViews.getTotalElements());
        return new PageInfoRes<>(pageUserRes);
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
    public List<CountMockTestTopicRes> countMockTestOfTopic(
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
