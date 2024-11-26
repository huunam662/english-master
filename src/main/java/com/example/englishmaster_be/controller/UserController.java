package com.example.englishmaster_be.controller;

import com.example.englishmaster_be.common.response.ExceptionResponseModel;
import com.example.englishmaster_be.common.response.ResponseModel;
import com.example.englishmaster_be.config.jwt.util.JwtUtil;
import com.example.englishmaster_be.dto.*;
import com.example.englishmaster_be.dto.user.ChangePassDTO;
import com.example.englishmaster_be.dto.user.ChangeProfileDTO;
import com.example.englishmaster_be.exception.response.ApiResponse;
import com.example.englishmaster_be.exception.response.ResponseUtil;
import com.example.englishmaster_be.model.*;
import com.example.englishmaster_be.model.response.*;
import com.example.englishmaster_be.model.response.AuthResponse;
import com.example.englishmaster_be.repository.*;
import com.example.englishmaster_be.service.*;
import com.example.englishmaster_be.service.impl.UserServiceImpl;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.*;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api")
@SuppressWarnings("unchecked")
@RequiredArgsConstructor
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserServiceImpl userService;

    @Autowired
    private IUserService IUserService;

    @Autowired
    private IOtpService IOtpService;

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
    JwtUtil jwtUtils;

    @Autowired
    private ResourceLoader resourceLoader;

    @Value("${masterE.linkFE}")
    private String linkFE;

    @Autowired
    private IInvalidTokenService IInvalidTokenService;

    @Autowired
    private IUploadService IUploadService;
    @Autowired
    private ContentRepository contentRepository;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody UserRegisterDTO registerDTO,
                                                              HttpServletRequest request) {
        return ResponseEntity.ok(
                ResponseUtil.success(userService.createUser(registerDTO)
                        , "Sent a confirmation mail!"
                        , request.getRequestURI()
                )
        );
    }

    @GetMapping("/register/confirm")
    public ResponseModel confirmRegister(@RequestParam("token") String confirmationToken) {

        ResponseModel responseModel = new ResponseModel();
        ConfirmationToken confirmToken = confirmationTokenRepository.findByCodeAndType(confirmationToken, "ACTIVE");

        ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
        if (confirmToken == null) {
            exceptionResponseModel.setMessage("Invalid verification code!");
            exceptionResponseModel.setStatus(HttpStatus.BAD_REQUEST);
            return exceptionResponseModel;
        }

        if (confirmToken.getUser().isEnabled()) {
            exceptionResponseModel.setMessage("Account has been verified!");
            exceptionResponseModel.setStatus(HttpStatus.BAD_REQUEST);
            return exceptionResponseModel;
        }

        if ((confirmToken.getCreateAt().plusMinutes(5)).isBefore(LocalDateTime.now())) {
            exceptionResponseModel.setMessage("Verification code has expired, Please register again!");
            exceptionResponseModel.setStatus(HttpStatus.BAD_REQUEST);
            return exceptionResponseModel;
        }

        User user = confirmToken.getUser();

        user.setEnabled(true);
        userRepository.save(user);

        responseModel.setMessage("Account has been successfully verified!");
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
            responseModel.setResponseData(authResponse);
            return responseModel;
        } catch (AuthenticationException e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("login fail");
            exceptionResponseModel.setStatus(HttpStatus.BAD_REQUEST);
            return exceptionResponseModel;
        }
    }

    @PostMapping("/forgetPassword")
    public ResponseModel forgetPassword(@RequestParam("email") String email) throws MessagingException, IOException {

        ResponseModel responseModel = new ResponseModel();

        boolean checkEmailExists = IUserService.existsEmail(email);

        ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();

        if (email == null || email.isEmpty()) {
            exceptionResponseModel.setMessage("Vui lòng điền email để được hỗ trợ");
            exceptionResponseModel.setStatus(HttpStatus.BAD_REQUEST);
            return exceptionResponseModel;
        }

        if (!checkEmailExists) {
            exceptionResponseModel.setMessage("Không tìm thấy email " + email);
            exceptionResponseModel.setStatus(HttpStatus.BAD_REQUEST);
            return exceptionResponseModel;
        }

        String otp = IOtpService.generateOtp(email);

        sendOtpToEmail(email, otp);

        responseModel.setMessage("Kiểm tra email của bạn để xác thực mã OTP.");

        return responseModel;
    }

    @PostMapping("/verifyOtp")
    public ResponseModel verifyOtp(@RequestParam String otp) {

        ResponseModel responseModel = new ResponseModel();

        ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();

        if (otp == null || otp.isEmpty()) {
            exceptionResponseModel.setMessage("OTP không được bỏ trống");
            exceptionResponseModel.setStatus(HttpStatus.BAD_REQUEST);
            return exceptionResponseModel;
        }

        boolean isOtpValid = IOtpService.validateOtp(otp);

        if (!isOtpValid) {
            exceptionResponseModel.setMessage("Mã OTP đã hết hiệu lực");
            exceptionResponseModel.setStatus(HttpStatus.BAD_REQUEST);
            return exceptionResponseModel;
        }

        IOtpService.updateOtpStatusToVerified(otp);
        responseModel.setMessage("Mã OTP đã được xác thực thành công.");


        return responseModel;
    }

    @PostMapping("/changePassword")
    public ResponseModel changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        ResponseModel responseModel = new ResponseModel();

        String otp = changePasswordDTO.getCode();
        String newPassword = changePasswordDTO.getNewPass();
        String confirmPassword = changePasswordDTO.getConfirmPass();

        // Regex để kiểm tra mật khẩu
        String regexPassword = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$";

        ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
        boolean isOtpValid = IOtpService.validateOtp(otp);

        if (!isOtpValid) {
            exceptionResponseModel.setMessage("Mã OTP đã hết hạn");
            exceptionResponseModel.setStatus(HttpStatus.BAD_REQUEST);
            return exceptionResponseModel;
        }

        if (newPassword == null || newPassword.isEmpty()) {
            exceptionResponseModel.setMessage("Mật khẩu mới không được bỏ trống");
            exceptionResponseModel.setStatus(HttpStatus.BAD_REQUEST);
            return exceptionResponseModel;
        }

        // Kiểm tra mật khẩu mới có đúng định dạng theo regex hay không
        if (!newPassword.matches(regexPassword)) {
            exceptionResponseModel.setMessage("Mật khẩu mới phải chứa ít nhất 1 chữ số, " +
                    "1 chữ thường, 1 chữ hoa, 1 ký tự đặc biệt và không được có khoảng trắng, " +
                    "độ dài từ 8 đến 20 ký tự.");
            exceptionResponseModel.setStatus(HttpStatus.BAD_REQUEST);
            return exceptionResponseModel;
        }

        if (!newPassword.equals(confirmPassword)) {
            exceptionResponseModel.setMessage("Mật khẩu mới và xác nhận mật khẩu không khớp");
            exceptionResponseModel.setStatus(HttpStatus.BAD_REQUEST);
            return exceptionResponseModel;
        }

        boolean isPasswordUpdated = IUserService.updatePassword(otp, newPassword);

        if (isPasswordUpdated) {
            IOtpService.deleteOtp(otp);
            responseModel.setMessage("Mật khẩu đã được thay đổi thành công.");

            return responseModel;
        } else {
            responseModel.setMessage("Không thể thay đổi mật khẩu. Vui lòng thử lại.");

            return responseModel;
        }
    }


    @GetMapping("/forgetPass/confirm")
    public ResponseModel confirmForgetPassword(@RequestParam String token) {
        ResponseModel responseModel = new ResponseModel();

        ConfirmationToken confirmToken = confirmationTokenRepository.findByCodeAndType(token, "RESET_PASSWORD");
        ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
        if (confirmToken == null) {
            exceptionResponseModel.setMessage("Invalid reset code!");
            exceptionResponseModel.setStatus(HttpStatus.BAD_REQUEST);
            return exceptionResponseModel;
        }

        if (confirmToken.getCreateAt().plusMinutes(5).isBefore(LocalDateTime.now())) {
            exceptionResponseModel.setMessage("Reset code has expired!");
            exceptionResponseModel.setStatus(HttpStatus.BAD_REQUEST);
            return exceptionResponseModel;
        }


        responseModel.setMessage("Successful!");
        responseModel.setResponseData(token);

        return responseModel;
    }


    @PostMapping("/refreshToken")
    public ResponseModel refreshToken(@RequestBody RefreshTokenDTO refreshTokenDTO) {
        String refresh = refreshTokenDTO.getRequestRefresh();
        ResponseModel responseModel = new ResponseModel();

        ConfirmationToken token = IRefreshTokenService.findByToken(refresh);

        ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();

        if (token == null) {
            exceptionResponseModel.setMessage("Refresh token isn't in database!");
            exceptionResponseModel.setStatus(HttpStatus.BAD_REQUEST);
            return exceptionResponseModel;
        }

        responseModel = IRefreshTokenService.verifyExpiration(responseModel, token);

        if (responseModel.getStatus() != null) {
            return responseModel;
        }

        String newToken = jwtUtils.generateTokenFromUsername(token.getUser().getEmail());

        JSONObject obj = new JSONObject();

        obj.put("accessToken", newToken);

        responseModel.setMessage("Created new access token");
        responseModel.setResponseData(obj);

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


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Information user fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @Transactional
    @PatchMapping(value = "/changeProfile", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> changeProfile(@ModelAttribute("profileUser") ChangeProfileDTO changeProfileDTO) {
        ResponseModel responseModel = new ResponseModel();
        JSONObject objectResponse = new JSONObject();
        try {
            User user = IUserService.currentUser();
            MultipartFile image = changeProfileDTO.getAvatar();
            if (image != null) {
                if (user.getAvatar() != null && !user.getAvatar().isEmpty() && user.getAvatar().startsWith("https://s3.meu-solutions.com/")) {
                        contentRepository.deleteByContentData(user.getAvatar());
                    }
                user.setAvatar(IUploadService.upload(image, "/", false, null, null));
            }
            if (changeProfileDTO.getName() != null && !changeProfileDTO.getName().isEmpty()) {
                user.setName(changeProfileDTO.getName());
            }
            if (changeProfileDTO.getAddress() != null && !changeProfileDTO.getAddress().isEmpty()) {
                user.setAddress(changeProfileDTO.getAddress());
            }
            if (changeProfileDTO.getPhone() != null && !changeProfileDTO.getPhone().isEmpty()) {
                user.setPhone(changeProfileDTO.getPhone());
            }
            IUserService.save(user);
            UserResponse userResponse = new UserResponse(user);
            objectResponse.put("Role", user.getRole().getRoleName().equals("ROLE_ADMIN") ? "ADMIN" : "USER");
            objectResponse.put("User", userResponse);
            responseModel.setResponseData(objectResponse);
            responseModel.setMessage("Change profile user successfully");

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Change profile user fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            log.warn(e.getMessage());
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }


    @PatchMapping(value = "/changePass")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> changePass(@RequestBody ChangePassDTO changePassDTO) {
        ResponseModel responseModel = new ResponseModel();
        ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
        try {
            User user = IUserService.currentUser();

            String regex = "^(?=.*[0-9])"
                    + "(?=.*[a-z])(?=.*[A-Z])"
                    + "(?=.*[@#$%^&+=])"
                    + "(?=\\S+$).{8,20}$";

            Pattern p = Pattern.compile(regex);

            if (!changePassDTO.getConfirmPass().equals(changePassDTO.getNewPass())) {
                exceptionResponseModel.setMessage("Password and confirm password don't match");
                exceptionResponseModel.setStatus(HttpStatus.BAD_REQUEST);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponseModel);
            }

            Matcher m = p.matcher(changePassDTO.getNewPass());
            if (!m.matches()) {
                exceptionResponseModel.setMessage("Password must contain at least 1 uppercase, 1 lowercase, 1 numeric, 1 special character and no spaces");
                exceptionResponseModel.setStatus(HttpStatus.BAD_REQUEST);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponseModel);
            }


            if (!passwordEncoder.matches(changePassDTO.getOldPass(), user.getPassword())) {
                exceptionResponseModel.setMessage("Old password don't correct");
                exceptionResponseModel.setStatus(HttpStatus.BAD_REQUEST);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponseModel);
            }

            if (passwordEncoder.matches(changePassDTO.getNewPass(), user.getPassword())) {
                exceptionResponseModel.setMessage("New password can't be the same as the old one");
                exceptionResponseModel.setStatus(HttpStatus.BAD_REQUEST);
                return ResponseEntity.status(HttpStatus.OK).body(exceptionResponseModel);
            }

            user.setPassword(passwordEncoder.encode(changePassDTO.getNewPass()));
            IUserService.save(user);

            responseModel.setMessage("Change pass user successfully");


            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            exceptionResponseModel.setMessage("Change pass user fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
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
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseModel> getExamResultsUser(@RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page,
                                                            @RequestParam(value = "size", defaultValue = "5") @Min(1) @Max(100) Integer size,
                                                            @RequestParam(value = "sortBy", defaultValue = "updateAt") String sortBy,
                                                            @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction sortDirection) {
        ResponseModel responseModel = new ResponseModel();

        try {
            User user = IUserService.currentUser();
            JSONObject responseObject = new JSONObject();

            OrderSpecifier<?> orderSpecifier;


            if (Sort.Direction.DESC.equals(sortDirection)) {
                orderSpecifier = QTopic.topic.updateAt.desc();
            } else {
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
            for (Topic topic : dataTopic) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Topic", new TopicResponse(topic));

                JPAQuery<MockTest> queryListMockTest = queryFactory.selectFrom(QMockTest.mockTest);
                queryListMockTest.where(QMockTest.mockTest.user.userId.eq(user.getUserId()));
                queryListMockTest.where(QMockTest.mockTest.topic.eq(topic));

                List<MockTestResponse> jsonMockTestArray = new ArrayList<>();
                for (MockTest mockTest : queryListMockTest.fetch()) {
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


            return ResponseEntity.status(HttpStatus.OK)
                    .body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Show list mock test result fail: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.EXPECTATION_FAILED));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseModel> logoutUser(@RequestBody UserLogoutDTO userLogoutDTO) {
        ResponseModel responseModel = new ResponseModel();

        try {
            User user = IUserService.currentUser();
            if (user != null) {
                IUserService.logoutUser();
            }

            IInvalidTokenService.insertInvalidToken(userLogoutDTO.getAccessToken());

            String refreshToken = userLogoutDTO.getRefreshToken();
            IRefreshTokenService.deleteRefreshToken(refreshToken);

            responseModel.setMessage("Logout successful");
            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (Exception e) {
            ExceptionResponseModel exceptionResponseModel = new ExceptionResponseModel();
            exceptionResponseModel.setMessage("Logout failed: " + e.getMessage());
            exceptionResponseModel.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            exceptionResponseModel.setViolations(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponseModel);
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

    private void sendOtpToEmail(String email, String otp) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        String otpMessage = "Ma OTP xac thuc la " + otp + ", hieu luc 10 phut";

        // Nếu bạn vẫn muốn sử dụng template, thay thế nội dung theo cách này:
        String templateContent = readTemplateContent("sendOtpEmail.html");
        templateContent = templateContent.replace("{{otpMessage}}", otpMessage);
        templateContent = templateContent.replace("{{btnConfirm}}", otp);
        templateContent = templateContent.replace("{{nameLink}}", "Xác thực mã OTP");

        helper.setTo(email);
        helper.setSubject("Quên mật khẩu");
        helper.setText(templateContent, true);
        mailSender.send(message);
    }

    private String readTemplateContent(String templateFileName) throws IOException {
        Resource templateResource = resourceLoader.getResource("classpath:templates/" + templateFileName);
        byte[] templateBytes = FileCopyUtils.copyToByteArray(templateResource.getInputStream());
        return new String(templateBytes, StandardCharsets.UTF_8);
    }
}
