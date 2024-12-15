package com.example.englishmaster_be.domain.exam.contorller;


import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.domain.exam.dto.response.ExamUserAccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Exam")
@RestController
@RequestMapping("/exam")
public class ExamController {

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DefaultMessage("UserEntity access")
    public ExamUserAccessResponse userAccess() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        return ExamUserAccessResponse.builder()
                .content("UserEntity ContentEntity. " + username)
                .build();
    }
}
