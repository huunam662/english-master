package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Configuration.jwt.JwtUtils;
import com.example.englishmaster_be.DTO.*;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.*;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private IUserService IUserService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private IRefreshTokenService IRefreshTokenService;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    private ResourceLoader resourceLoader;

    @PostMapping("/register")
    public ResponseModel register(@RequestBody UserRegisterDTO registerDTO) throws IOException, MessagingException {
        ResponseModel responseModel = new ResponseModel();

        User user = IUserService.createUser(registerDTO);

        boolean existingUser = userRepository.existsByEmail(user.getEmail());

        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            responseModel.setMessage("Password and confirm password don't match");
            responseModel.setStatus("fail");
            return responseModel;
        }

        if (existingUser) {
            responseModel.setMessage("This email already exists!");
            responseModel.setStatus("fail");
            return responseModel;
        }


        userRepository.save(user);

        ConfirmationToken confirmationToken = new ConfirmationToken(user);
        confirmationToken.setType("ACTIVE");
        confirmationToken.setCode(UUID.randomUUID().toString());

        confirmationTokenRepository.save(confirmationToken);

        sendConfirmationEmail(user.getEmail(), confirmationToken.getCode().toString());

        responseModel.setMessage("Sent a confirmation mail!");
        responseModel.setStatus("success");

        return responseModel;
    }

    @GetMapping("/register/confirm")
    public ResponseModel confirmRegister(@RequestParam("token") String confirmationToken) {

        ResponseModel responseModel = new ResponseModel();
        ConfirmationToken confirmToken = confirmationTokenRepository.findByCodeAndType(confirmationToken, "ACTIVE");
        if (confirmToken == null) {
            responseModel.setMessage("Invalid verification code!");
            responseModel.setStatus("fail");
            return responseModel;
        }

        if (confirmToken.getUser().isEnabled()) {
            responseModel.setMessage("Account has been verified!");
            responseModel.setStatus("fail");
            return responseModel;
        }

        if ((confirmToken.getCreateAt().plusMinutes(5)).isBefore(LocalDateTime.now())) {
            responseModel.setMessage("Verification code has expired!");
            responseModel.setStatus("fail");
            return responseModel;
        }

        User user = confirmToken.getUser();

        user.setEnabled(true);
        userRepository.save(user);

        responseModel.setMessage("Account has been successfully verified!");
        responseModel.setStatus("success");
        return responseModel;
    }

    @PostMapping("/login")
    public ResponseModel login(@RequestBody UserLoginDTO loginDTO) {
        ResponseModel responseModel = new ResponseModel();
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            String jwt = jwtUtils.generateJwtToken(userDetails);

            User user = IUserService.findUser(userDetails);

            IRefreshTokenService.deleteAllTokenExpired(user);

            ConfirmationToken refreshToken = IRefreshTokenService.createRefreshToken(userDetails.getUsername());

            AuthResponse authResponse = new AuthResponse(jwt, refreshToken.getCode());

            responseModel.setMessage("login successful");
            responseModel.setStatus("success");
            responseModel.setResponseData(authResponse);
            return responseModel;
        } catch (AuthenticationException e) {
            responseModel.setMessage("login fail");
            responseModel.setStatus("fail");
            return responseModel;
        }
    }

    @PostMapping("/forgetPassword")
    public ResponseModel forgetPassword(@RequestParam("email") String email) throws MessagingException, IOException {
        ResponseModel responseModel = new ResponseModel();

        boolean existingUser = userRepository.existsByEmail(email);

        if (!existingUser) {
            responseModel.setMessage("This email don't exists!");
            responseModel.setStatus("fail");
            return responseModel;
        }

        User user = userRepository.findByEmail(email);

        ConfirmationToken confirmationToken = new ConfirmationToken(user);
        confirmationToken.setType("RESET_PASSWORD");

        String format3 = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyyHHmmss"));
        String generatedString = RandomStringUtils.randomAlphabetic(10) + format3;

        confirmationToken.setCode(generatedString);

        confirmationTokenRepository.save(confirmationToken);

        sendForgetPassEmail(user.getEmail(), generatedString);

        responseModel.setMessage("Sent a reset password mail!");
        responseModel.setStatus("success");

        return responseModel;
    }

    @GetMapping("/forgetPass/confirm")
    public ResponseModel confirmForgetPassword(@RequestParam String token) {
        ResponseModel responseModel = new ResponseModel();

        ConfirmationToken confirmToken = confirmationTokenRepository.findByCodeAndType(token, "RESET_PASSWORD");

        if (confirmToken == null) {
            responseModel.setMessage("Invalid reset code!");
            responseModel.setStatus("fail");
            return responseModel;
        }

        if (confirmToken.getCreateAt().plusMinutes(5).isBefore(LocalDateTime.now())) {
            responseModel.setMessage("Reset code has expired!");
            responseModel.setStatus("fail");
            return responseModel;
        }

        responseModel.setStatus("success");
        responseModel.setMessage("Successful!");
        responseModel.setResponseData(token);

        return responseModel;
    }

    @PostMapping("/changePassword")
    public ResponseModel changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        ResponseModel responseModel = new ResponseModel();

        ConfirmationToken confirmToken = confirmationTokenRepository.findByCodeAndType(changePasswordDTO.getCode(), "RESET_PASSWORD");

        if (confirmToken == null) {
            responseModel.setMessage("Invalid reset code!");
            responseModel.setStatus("fail");
            return responseModel;
        }

        if (confirmToken.getCreateAt().plusMinutes(5).isBefore(LocalDateTime.now())) {
            responseModel.setMessage("Reset code has expired!");
            responseModel.setStatus("fail");
            return responseModel;
        }

        if (!changePasswordDTO.getConfirmPass().equals(changePasswordDTO.getNewPass())) {
            responseModel.setMessage("New pass and confirm pass don't match!");
            responseModel.setStatus("fail");
            return responseModel;
        }

        User user = confirmToken.getUser();
        IUserService.changePassword(user, changePasswordDTO.getNewPass());

        userRepository.save(user);
        confirmationTokenRepository.delete(confirmToken);

        responseModel.setMessage("Changed password!");
        responseModel.setStatus("success");

        return responseModel;
    }

    @PostMapping("/refreshToken")
    public ResponseModel refreshToken(@RequestBody RefreshTokenDTO refreshTokenDTO) {
        String refresh = refreshTokenDTO.getRequestRefresh();
        ResponseModel responseModel = new ResponseModel();

        ConfirmationToken token = IRefreshTokenService.findByToken(refresh);

        if (token == null) {
            responseModel.setMessage("Refresh token isn't in database!");
            responseModel.setStatus("fail");
            return responseModel;
        }

        responseModel = IRefreshTokenService.verifyExpiration(responseModel, token);

        if (responseModel.getStatus() != null) {
            return responseModel;
        }

        String newToken = jwtUtils.generateTokenFromUsername(token.getUser().getEmail());

        JSONObject obj = new JSONObject();

        obj.put("accessToken", newToken);

        responseModel.setStatus("success");
        responseModel.setMessage("Created new access token");
        responseModel.setResponseData(obj);

        return responseModel;
    }

    @GetMapping("/logout")
    public ResponseModel logoutUser(@RequestParam String access_token) {
        ResponseModel responseModel = new ResponseModel();

        User user = IUserService.currentUser();

        responseModel.setStatus("success");
        responseModel.setMessage("Log out successful");

        return responseModel;
    }

    @GetMapping("/information")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> informationUser() {
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.currentUser();

            UserResponse userResponse = new UserResponse(user);

            responseModel.setResponseData(userResponse);
            responseModel.setMessage("Information user successfully");
            responseModel.setStatus("success");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Information user fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }


    private void sendConfirmationEmail(String email, String confirmationToken) throws IOException, MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        String confirmationLink = "http://localhost:8080/api/register/confirm?token=" + confirmationToken;


        String templateContent = readTemplateContent("email_templates.html");
        templateContent = templateContent.replace("{{linkConfirm}}", confirmationLink);
        templateContent = templateContent.replace("{{btnConfirm}}", "Confirm");
        templateContent = templateContent.replace("{{nameLink}}", "Confirm Register");


        helper.setTo(email);
        helper.setSubject("Xác nhận tài khoản");
        helper.setText(templateContent, true);
        mailSender.send(message);
    }

    private void sendForgetPassEmail(String email, String confirmationToken) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        String confirmationLink = "http://localhost:8080/api/forgetPass/confirm?token=" + confirmationToken;

        String templateContent = readTemplateContent("email_templates.html");
        templateContent = templateContent.replace("{{linkConfirm}}", confirmationLink);
        templateContent = templateContent.replace("{{btnConfirm}}", "Reset Password");
        templateContent = templateContent.replace("{{nameLink}}", "Reset Password");

        helper.setTo(email);
        helper.setSubject("Quên tài khoản");
        helper.setText(templateContent, true);
        mailSender.send(message);
    }

    private String readTemplateContent(String templateFileName) throws IOException {
        Resource templateResource = resourceLoader.getResource("classpath:templates/" + templateFileName);
        byte[] templateBytes = FileCopyUtils.copyToByteArray(templateResource.getInputStream());
        return new String(templateBytes, StandardCharsets.UTF_8);
    }
}
