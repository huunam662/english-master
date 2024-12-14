package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Common.dto.response.FilterResponse;
import com.example.englishmaster_be.Configuration.global.annotation.MessageResponse;
import com.example.englishmaster_be.Model.Request.User.UserFilterRequest;
import com.example.englishmaster_be.Model.Response.CountMockTestTopicResponse;
import com.example.englishmaster_be.Service.*;
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


    IUserService userService;


    @GetMapping(value = "/getAllUser")
    @PreAuthorize("hasRole('ADMIN')")
    @MessageResponse("List UserEntity successfully")
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

        return userService.getAllUser(userFilterRequest);

    }

    @PatchMapping(value = "/{userId:.+}/enableUser")
    @PreAuthorize("hasRole('ADMIN')")
    public void enableUser(@PathVariable UUID userId, @RequestParam Boolean enable) {

        userService.enableUser(userId, enable);
    }

    @DeleteMapping(value = "/{userId:.+}/deleteUser")
    @PreAuthorize("hasRole('ADMIN')")
    @MessageResponse("Delete account of UserEntity successfully")
    public void deleteUser(@PathVariable UUID userId) {

        userService.deleteUser(userId);
    }

    @GetMapping(value = "/countMockTestOfTopic")
    @PreAuthorize("hasRole('ADMIN')")
    @MessageResponse("List TopicEntity and count mock test successfully")
    public List<CountMockTestTopicResponse> countMockTestOfTopic(
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "pack") UUID packId
    ) {

        return userService.getCountMockTestOfTopic(date, packId);
    }
}
