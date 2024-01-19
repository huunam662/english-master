package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Configuration.jwt.JwtUtils;
import com.example.englishmaster_be.DTO.*;
import com.example.englishmaster_be.DTO.User.*;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.*;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.*;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.*;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api")
@SuppressWarnings("unchecked")
public class UserController {
    @Autowired
    private IUserService IUserService;

    @Autowired
    private IFileStorageService IFileStorageService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private IRefreshTokenService IRefreshTokenService;
    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    private ResourceLoader resourceLoader;
    @Value("${masterE.linkFE}")
    private String linkFE;

    @PostMapping("/register")
    public ResponseModel register(@RequestBody UserRegisterDTO registerDTO) throws IOException, MessagingException {
        ResponseModel responseModel = new ResponseModel();

        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=])"
                + "(?=\\S+$).{8,20}$";

        Pattern p = Pattern.compile(regex);

        if (registerDTO.getPassword() == null){
            responseModel.setMessage("Password is null");
            responseModel.setStatus("fail");
            return responseModel;
        }

        Matcher m = p.matcher(registerDTO.getPassword());
        if(!m.matches()){
            responseModel.setMessage("Password must contain at least 1 uppercase, 1 lowercase, 1 numeric, 1 special character and no spaces");
            responseModel.setStatus("fail");
            return responseModel;
        }

        User user = IUserService.createUser(registerDTO);

        boolean existingUser = userRepository.existsByEmail(user.getEmail());

        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            responseModel.setMessage("Password and confirm password don't match");
            responseModel.setStatus("fail");
            return responseModel;
        }

        if (existingUser) {
            User userExist = IUserService.findeUserByEmail(user.getEmail());
            if(!userExist.isEnabled()){
                userRepository.delete(userExist);
            }else {
                responseModel.setMessage("This email already exists!");
                responseModel.setStatus("fail");
                return responseModel;
            }

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
    @PreAuthorize("hasRole('USER')")
    public ResponseModel logoutUser(@RequestParam String access_token) {
        ResponseModel responseModel = new ResponseModel();

        User user = IUserService.currentUser();

        responseModel.setStatus("success");
        responseModel.setMessage("Log out successful");

        return responseModel;
    }

    @GetMapping("/information")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> informationUser() {
        ResponseModel responseModel = new ResponseModel();
        JSONObject objectResponse = new JSONObject();
        try {
            User user = IUserService.currentUser();
            if (user.getRole().getRoleName().equals("ROLE_ADMIN")) {
                objectResponse.put("Role", "ADMIN");
            } else {
                objectResponse.put("Role", "USER");
            }

            UserResponse userResponse = new UserResponse(user);
            objectResponse.put("User", userResponse);

            responseModel.setResponseData(objectResponse);
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

    @PatchMapping(value ="/changeProfile", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('USER')" )
    public ResponseEntity<ResponseModel> changeProfile(@ModelAttribute("profileUser") ChangeProfileDTO changeProfileDTO) {
        ResponseModel responseModel = new ResponseModel();
        JSONObject objectResponse = new JSONObject();
        try {
            User user = IUserService.currentUser();

            String filename = null;

            MultipartFile image = changeProfileDTO.getAvatar();

            if (image != null && !image.isEmpty()) {
                filename = IFileStorageService.nameFile(image);
            }

            if(!changeProfileDTO.getName().isEmpty()){
                user.setName(changeProfileDTO.getName());
            }

            if(!changeProfileDTO.getAddress().isEmpty()){
                user.setAddress(changeProfileDTO.getAddress());
            }
            if(!changeProfileDTO.getPhone().isEmpty()){
                user.setPhone(changeProfileDTO.getPhone());
            }

            if(filename != null){
                if(user.getAvatar() != null){
                    IFileStorageService.delete(user.getAvatar());
                }
                user.setAvatar(filename);
            }
            IUserService.save(user);
            if(filename != null){
                IFileStorageService.save(changeProfileDTO.getAvatar(), filename);
            }

            UserResponse userResponse = new UserResponse(user);
            if (user.getRole().getRoleName().equals("ROLE_ADMIN")) {
                objectResponse.put("Role", "ADMIN");
            } else {
                objectResponse.put("Role", "USER");
            }
            objectResponse.put("User", userResponse);

            responseModel.setResponseData(objectResponse);
            responseModel.setMessage("Change profile user successfully");
            responseModel.setStatus("success");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Change profile user fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @PatchMapping(value ="/changePass")
    @PreAuthorize("hasRole('USER')" )
    public ResponseEntity<ResponseModel> changePass(@RequestBody ChangePassDTO changePassDTO) {
        ResponseModel responseModel = new ResponseModel();
        try {
            User user = IUserService.currentUser();

            String regex = "^(?=.*[0-9])"
                    + "(?=.*[a-z])(?=.*[A-Z])"
                    + "(?=.*[@#$%^&+=])"
                    + "(?=\\S+$).{8,20}$";

            Pattern p = Pattern.compile(regex);

            if(!changePassDTO.getConfirmPass().equals(changePassDTO.getNewPass())){
                responseModel.setMessage("Password and confirm password don't match");
                responseModel.setStatus("fail");
                return ResponseEntity.status(HttpStatus.OK).body(responseModel);
            }

            Matcher m = p.matcher(changePassDTO.getNewPass());
            if(!m.matches()){
                responseModel.setMessage("Password must contain at least 1 uppercase, 1 lowercase, 1 numeric, 1 special character and no spaces");
                responseModel.setStatus("fail");
                return ResponseEntity.status(HttpStatus.OK).body(responseModel);
            }


            if(!passwordEncoder.matches(changePassDTO.getOldPass(), user.getPassword())){
                responseModel.setMessage("Old password don't correct");
                responseModel.setStatus("fail");
                return ResponseEntity.status(HttpStatus.OK).body(responseModel);
            }

            if (passwordEncoder.matches(changePassDTO.getNewPass(), user.getPassword())) {
                responseModel.setMessage("New password can't be the same as the old one");
                responseModel.setStatus("fail");
                return ResponseEntity.status(HttpStatus.OK).body(responseModel);
            }

            user.setPassword(passwordEncoder.encode(changePassDTO.getNewPass()));
            IUserService.save(user);

            responseModel.setMessage("Change pass user successfully");
            responseModel.setStatus("success");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Change pass user fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }


    private void sendConfirmationEmail(String email, String confirmationToken) throws IOException, MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        String confirmationLink = linkFE + "register/confirm?token=" + confirmationToken;



        String templateContent = readTemplateContent("email_templates.html");
        templateContent = templateContent.replace("{{linkConfirm}}", confirmationLink);
        templateContent = templateContent.replace("{{btnConfirm}}", "Confirm");
        templateContent = templateContent.replace("{{nameLink}}", "Confirm Register");


        helper.setTo(email);
        helper.setSubject("Xác nhận tài khoản");
        helper.setText(templateContent, true);
        mailSender.send(message);
    }

    @GetMapping(value = "/listExamResultsUser")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseModel> getExamResultsUser(@RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page,
                                                     @RequestParam(value = "size", defaultValue = "5") @Min(1) @Max(100) Integer size,
                                                     @RequestParam(value = "sortBy", defaultValue = "updateAt") String sortBy,
                                                     @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction sortDirection) {
        ResponseModel responseModel = new ResponseModel();

        try {
            User user = IUserService.currentUser();
            JSONObject responseObject = new JSONObject();

            OrderSpecifier<?> orderSpecifier;


            if(Sort.Direction.DESC.equals(sortDirection)){
                orderSpecifier = QTopic.topic.updateAt.desc();
            }else {
                orderSpecifier = QTopic.topic.updateAt.asc();
            }

            JPAQuery<Topic> queryMockTest = queryFactory.select(QMockTest.mockTest.topic)
                    .from(QMockTest.mockTest)
                    .where(QMockTest.mockTest.user.userId.eq(user.getUserId()))
                    .groupBy(QMockTest.mockTest.topic);

            List<Topic> listTopicUser = queryMockTest.fetch();

            JPAQuery<Topic> query = queryFactory.selectFrom(QTopic.topic)
                    .orderBy(orderSpecifier)
                    .offset((long) page * size)
                    .limit(size);
            query.where(QTopic.topic.in(listTopicUser));

            List<Topic> dataTopic = query.fetch();
            List<JSONObject> jsonTopicArray = new ArrayList<>();
            for(Topic topic: dataTopic){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Topic", new TopicResponse(topic));

                JPAQuery<MockTest> queryListMockTest = queryFactory.selectFrom(QMockTest.mockTest);
                queryListMockTest.where(QMockTest.mockTest.user.userId.eq(user.getUserId()));
                queryListMockTest.where(QMockTest.mockTest.topic.eq(topic));

                List<MockTestResponse> jsonMockTestArray = new ArrayList<>();
                for(MockTest mockTest: queryListMockTest.fetch()){
                    jsonMockTestArray.add(new MockTestResponse(mockTest));
                }
                jsonObject.put("listMockTest", jsonMockTestArray);

                jsonTopicArray.add(jsonObject);
            }

            long totalRecords = query.fetchCount();
            long totalPages = (long) Math.ceil((double) totalRecords / size);
            responseObject.put("totalPage", totalPages);
            responseObject.put("totalRecords", totalRecords);
            responseObject.put("listTopicUser", jsonTopicArray);

            responseModel.setMessage("Show list mock test result successfully");
            responseModel.setResponseData(responseObject);
            responseModel.setStatus("success");


            return ResponseEntity.status(HttpStatus.OK)
                    .body(responseModel);
        } catch (Exception e) {
            responseModel.setMessage("Show list mock test result fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    @PostMapping("/auth/signout")
    public ResponseEntity<ResponseModel> logoutUser(@RequestBody RefreshTokenDTO refreshTokenDTO) {

        ResponseModel responseModel = new ResponseModel();
        try {
            String refresh = refreshTokenDTO.getRequestRefresh();
            IRefreshTokenService.deleteRefreshToken(refresh);

            responseModel.setMessage("Sign out successful");
            responseModel.setStatus("success");
            return ResponseEntity.status(HttpStatus.OK)
                        .body(responseModel);
        }catch (AuthenticationException e) {
            responseModel.setMessage("Sign out fail: " + e.getMessage());
            responseModel.setStatus("fail");
            responseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED.value()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
        }
    }

    private void sendForgetPassEmail(String email, String confirmationToken) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        String confirmationLink = linkFE + "/forgetPass/confirm?token=" + confirmationToken;

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
