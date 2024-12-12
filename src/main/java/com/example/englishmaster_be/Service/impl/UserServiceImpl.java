package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Common.dto.response.FilterResponse;
import com.example.englishmaster_be.Configuration.global.thread.MessageResponseHolder;
import com.example.englishmaster_be.Configuration.jwt.JwtUtils;
import com.example.englishmaster_be.Common.enums.RoleEnum;
import com.example.englishmaster_be.Exception.template.CustomException;
import com.example.englishmaster_be.Mapper.MockTestMapper;
import com.example.englishmaster_be.Mapper.TopicMapper;
import com.example.englishmaster_be.Model.Request.*;
import com.example.englishmaster_be.Model.Request.ConfirmationToken.ConfirmationTokenRequest;
import com.example.englishmaster_be.Model.Request.User.ChangeProfileRequest;
import com.example.englishmaster_be.Model.Request.User.UserFilterRequest;
import com.example.englishmaster_be.Common.enums.ErrorEnum;
import com.example.englishmaster_be.Exception.template.BadRequestException;
import com.example.englishmaster_be.Mapper.UserMapper;
import com.example.englishmaster_be.Model.Response.*;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.*;
import com.example.englishmaster_be.entity.*;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Sort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements IUserService {


    @Value("${masterE.linkFE}")
    static String linkFE;

    JPAQueryFactory queryFactory;

    JwtUtils jwtUtils;

    JavaMailSender mailSender;

    AuthenticationManager authenticationManager;

    PasswordEncoder passwordEncoder;

    ResourceLoader resourceLoader;

    OtpRepository otpRepository;

    RoleRepository roleRepository;

    UserRepository userRepository;

    ConfirmationTokenRepository confirmationTokenRepository;

    ContentRepository contentRepository;

    IInvalidTokenService iInvalidTokenService;

    IRefreshTokenService iRefreshTokenService;

    IConfirmationTokenService confirmationTokenService;

    IOtpService otpService;

    ITopicService topicService;

    IUploadService uploadService;

    IMockTestService mockTestService;

    IPackService packService;

    IRefreshTokenService refreshTokenService;



    @Transactional
    @SneakyThrows
    @Override
    public void registerUser(UserRegisterRequest userRegisterDTO) {

        UserEntity user = userRepository.findByEmail(userRegisterDTO.getEmail()).orElse(null);

        if (user != null && !user.getEnabled())
            userRepository.delete(user);

        user = UserMapper.INSTANCE.toUserEntity(userRegisterDTO);
        user.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
        user.setRole(roleRepository.findByRoleName(RoleEnum.USER.name()));

        user = userRepository.save(user);

        ConfirmationTokenResponse confirmationTokenResponse = confirmationTokenService.createConfirmationToken(
                ConfirmationTokenRequest.builder().user(user).build()
        );

        try {
            sendConfirmationEmail(user.getEmail(), confirmationTokenResponse.getCode());
        } catch (IOException | MessagingException e) {
            throw new CustomException(ErrorEnum.SEND_EMAIL_FAILURE);
        }
    }

    @Transactional
    @Override
    public void confirmRegister(String confirmationToken) {

        ConfirmationTokenEntity confirmToken = confirmationTokenRepository.findByCodeAndType(confirmationToken, "ACTIVE");

        if (confirmToken == null)
            throw new BadRequestException("Invalid verification code");

        if (confirmToken.getUser().getEnabled())
            throw new BadRequestException("Account has been verified");

        if ((confirmToken.getCreateAt().plusMinutes(5)).isBefore(LocalDateTime.now()))
            throw new BadRequestException("Verification code has expired, Please register again");

        UserEntity user = confirmToken.getUser();

        user.setEnabled(Boolean.TRUE);

        userRepository.save(user);
    }

    @Override
    public AuthResponse login(UserLoginRequest userLoginDTO) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLoginDTO.getEmail(), userLoginDTO.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if(!userDetails.isEnabled())
            throw new DisabledException(ErrorEnum.ACCOUNT_DISABLED.getMessage());

        String jwt = jwtUtils.generateJwtToken(userDetails);

        UserEntity user = findUser(userDetails);

        refreshTokenService.deleteAllTokenExpired(user);

        ConfirmationTokenEntity refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());

        return AuthResponse.builder()
                .accessToken(jwt)
                .refreshToken(refreshToken.getCode())
                .build();
    }

    @Transactional
    @SneakyThrows
    @Override
    public void forgotPassword(String email) {

        if (email == null || email.isEmpty())
            throw new BadRequestException("Please fill your email");

        boolean emailExisting = existsEmail(email);

        if(!emailExisting)
            throw new BadRequestException("Your email isn't found");

        String otp = otpService.generateOtp(email);

        sendOtpToEmail(email, otp);

    }

    protected void sendOtpToEmail(String email, String otp) throws MessagingException, IOException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        String otpMessage = "This is a OTP code for your verify and it's valid in 1 minute";
        String otpTemplate = "<b>" + otp + "</b>";

        // Nếu bạn vẫn muốn sử dụng template, thay thế nội dung theo cách này:
        String templateContent = readTemplateContent("sendOtpEmail.html");
        templateContent = templateContent.replace("{{otpMessage}}", otpMessage)
                                        .replace("{{btnConfirm}}", otpTemplate)
                                        .replace("{{nameLink}}", "Verify by OTP");

        helper.setTo(email);
        helper.setSubject("Forgot your password");
        helper.setText(templateContent, true);
        mailSender.send(message);
    }


    protected void sendForgetPassEmail(String email, String confirmationToken) throws MessagingException, IOException {
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

    @Transactional
    @Override
    public void verifyOtp(String otp) {

        if (otp == null || otp.isEmpty())
            throw new BadRequestException("Please fill your otp");

        boolean isOtpValid = otpService.validateOtp(otp);

        if (!isOtpValid)
            throw new BadRequestException("The OTP code is expired");

        otpService.updateOtpStatusToVerified(otp);

    }

    @Transactional
    @Override
    public void changePassword(ChangePasswordRequest changePasswordDTO) {

        String otp = changePasswordDTO.getCode();
        String newPassword = changePasswordDTO.getNewPass();
        String confirmPassword = changePasswordDTO.getConfirmPass();

        // Regex để kiểm tra mật khẩu
        String regexPassword = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$";

        if (!otpService.validateOtp(otp))
            throw new BadRequestException("The OTP code is expired");

        if (newPassword == null || newPassword.isEmpty() || confirmPassword == null || confirmPassword.isEmpty())
            throw new BadRequestException("Please fill your password and your confirm password");

        // Kiểm tra mật khẩu mới có đúng định dạng theo regex hay không
        if (!newPassword.matches(regexPassword))
            throw new BadRequestException("The new password must contain at least 1 digit, " +
                    "1 lowercase letter, 1 uppercase letter, 1 special character, " +
                    "and must not contain any spaces, with a length of 8 to 20 characters.");

        if (!newPassword.equals(confirmPassword))
            throw new BadRequestException("The new password doesn't match confirm password");

        boolean isPasswordUpdated = updatePassword(otp, newPassword);

        if (!isPasswordUpdated)
            throw new BadRequestException("Cannot update your password, please try again");

        otpService.deleteOtp(otp);
    }

    @Override
    public String confirmForgetPassword(String token) {

        ConfirmationTokenEntity confirmToken = confirmationTokenRepository.findByCodeAndType(token, "RESET_PASSWORD");

        if (confirmToken == null)
            throw new BadRequestException("Invalid reset code");

        if (confirmToken.getCreateAt().plusMinutes(5).isBefore(LocalDateTime.now()))
            throw new BadRequestException("Reset code has expired");

        return token;
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest refreshTokenDTO) {

        String refresh = refreshTokenDTO.getRequestRefresh();

        ConfirmationTokenEntity token = refreshTokenService.findByToken(refresh);

        if (token == null)
            throw new BadRequestException("Refresh token isn't existed");

        refreshTokenService.verifyExpiration(token);

        String newToken = jwtUtils.generateTokenFromUsername(token.getUser().getEmail());

        return AuthResponse.builder()
                .accessToken(newToken)
                .build();
    }

    @Transactional
    @Override
    public UserEntity changeProfile(ChangeProfileRequest changeProfileRequest) {

        UserEntity user = currentUser();

        if (changeProfileRequest.getAvatar() != null && !changeProfileRequest.getAvatar().isEmpty()) {
            if (user.getAvatar() != null && !user.getAvatar().isEmpty() && user.getAvatar().startsWith("https://s3.meu-solutions.com/"))
                contentRepository.deleteByContentData(user.getAvatar());

            String avatarPathResponse = uploadService.upload(changeProfileRequest.getAvatar(), "/", false, null, null);

            user.setAvatar(avatarPathResponse);
        }

        UserMapper.INSTANCE.flowToUserEntity(changeProfileRequest, user);

        return userRepository.save(user);
    }

    @Transactional
    @Override
    public void changePass(com.example.englishmaster_be.Model.Request.User.ChangePasswordRequest changePassDTO) {

        UserEntity user = currentUser();

        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=])"
                + "(?=\\S+$).{8,20}$";

        Pattern pattern = Pattern.compile(regex);

        Matcher matcherOldPassword = pattern.matcher(changePassDTO.getOldPass());
        Matcher matcherNewPassword = pattern.matcher(changePassDTO.getNewPass());
        Matcher matcherConfirmPassword = pattern.matcher(changePassDTO.getConfirmPass());

        if(!matcherOldPassword.matches())
            throw new BadRequestException("Old password must be contain at least 1 uppercase, 1 lowercase, 1 numeric, 1 special character and no spaces");

        if (!matcherNewPassword.matches())
            throw new BadRequestException("New password must be contain at least 1 uppercase, 1 lowercase, 1 numeric, 1 special character and no spaces");

        if (!matcherConfirmPassword.matches())
            throw new BadRequestException("The confirm password must be contain at least 1 uppercase, 1 lowercase, 1 numeric, 1 special character and no spaces");

        if (!changePassDTO.getConfirmPass().equals(changePassDTO.getNewPass()))
            throw new BadRequestException("Your new password doesn't match with your confirm password");

        if (!passwordEncoder.matches(changePassDTO.getOldPass(), user.getPassword()))
            throw new BadRequestException("Your old password doesn't correct");

        if (passwordEncoder.matches(changePassDTO.getNewPass(), user.getPassword()))
            throw new BadRequestException("New password can't be the same as old password");

        user.setPassword(passwordEncoder.encode(changePassDTO.getNewPass()));

        userRepository.save(user);

    }


    @Override
    public FilterResponse<?> getExamResultsUser(UserFilterRequest filterRequest) {

        FilterResponse<ExamResultResponse> filterResponse = FilterResponse.<ExamResultResponse>builder()
                .pageNumber(filterRequest.getPage())
                .pageSize(filterRequest.getSize())
                .offset((long) (filterRequest.getPage() - 1) * filterRequest.getSize())
                .build();

        UserEntity user = currentUser();

        JPAQuery<TopicEntity> queryMockTest = queryFactory.select(QMockTestEntity.mockTestEntity.topic)
                                                    .from(QMockTestEntity.mockTestEntity)
                                                    .where(QMockTestEntity.mockTestEntity.user.userId.eq(user.getUserId()))
                                                    .groupBy(QMockTestEntity.mockTestEntity.topic);

        List<TopicEntity> listTopicUser = queryMockTest.fetch();

        BooleanExpression wherePatternOfTopic = QTopicEntity.topicEntity.in(listTopicUser);

        long totalElements = Optional.ofNullable(
                queryFactory.select(QTopicEntity.topicEntity.count())
                            .from(QTopicEntity.topicEntity)
                            .where(wherePatternOfTopic)
                            .fetchOne()
                        ).orElse(0L);
        long totalPages = (long) Math.ceil((double) totalElements / filterResponse.getPageSize());
        filterResponse.setTotalElements(totalElements);
        filterResponse.setTotalPages(totalPages);

        OrderSpecifier<?> orderSpecifier;

        if (Sort.Direction.DESC.equals(filterRequest.getSortDirection()))
            orderSpecifier = QTopicEntity.topicEntity.updateAt.desc();
        else orderSpecifier = QTopicEntity.topicEntity.updateAt.asc();

        JPAQuery<TopicEntity> query = queryFactory
                                .selectFrom(QTopicEntity.topicEntity)
                                .where(wherePatternOfTopic)
                                .orderBy(orderSpecifier)
                                .offset(filterResponse.getOffset())
                                .limit(filterResponse.getPageSize());

        filterResponse.setContent(
                query.fetch().stream().map(
                        topic -> {
                            ExamResultResponse examResultResponse = ExamResultResponse.builder()
                                    .topic(TopicMapper.INSTANCE.toTopicResponse(topic))
                                    .build();

                            JPAQuery<MockTestEntity> queryListMockTest = queryFactory.selectFrom(QMockTestEntity.mockTestEntity)
                                    .where(QMockTestEntity.mockTestEntity.user.userId.eq(user.getUserId()))
                                    .where(QMockTestEntity.mockTestEntity.topic.eq(topic));

                            examResultResponse.setListMockTest(
                                    MockTestMapper.INSTANCE.toMockTestResponseList(queryListMockTest.fetch())
                            );

                            return examResultResponse;
                        }
                ).toList()
        );

        return filterResponse;
    }

    @Override
    public void logoutUserOf(UserLogoutRequest userLogoutDTO) {

        boolean logoutSuccessfully = logoutUser();

        if(!logoutSuccessfully)
            throw new AuthenticationServiceException("You aren't logged in");

        iInvalidTokenService.insertInvalidToken(userLogoutDTO.getAccessToken());

        String refreshToken = userLogoutDTO.getRefreshToken();

        iRefreshTokenService.deleteRefreshToken(refreshToken);

    }

    @Transactional
    @Override
    public void deleteUser(UUID userId) {

        UserEntity userEntity = findUserById(userId);

        userRepository.delete(userEntity);
    }


    @Override
    public UserEntity findUser(UserDetails userDetails) {
        return findUserByEmail(userDetails.getUsername());
    }

    @Override
    public UserEntity currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails)
            return findUser(userDetails);

        throw new AuthenticationServiceException("Please authenticate user");
    }

    @Override
    public UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new BadRequestException("UserEntity not found")
        );
    }

    @Override
    public UserEntity findUserById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("UserEntity not found")
        );
    }

    @Override
    public boolean logoutUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            SecurityContextHolder.getContext().setAuthentication(null);
            return true;
        }

        return false;
    }

    @Override
    public boolean existsEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean updatePassword(String otp, String newPassword) {

        OtpEntity otpRecord = otpRepository.findById(otp).orElse(null);

        if (otpRecord == null || !"Verified".equals(otpRecord.getStatus()))
            return false;

        UserEntity user = userRepository.findByEmail(otpRecord.getEmail()).orElse(null);

        if (user == null) return false;

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        otpRecord.setStatus("Used");
        otpRepository.save(otpRecord);

        return true;
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

    protected String readTemplateContent(String templateFileName) throws IOException {
        Resource templateResource = resourceLoader.getResource("classpath:templates/" + templateFileName);
        byte[] templateBytes = FileCopyUtils.copyToByteArray(templateResource.getInputStream());
        return new String(templateBytes, StandardCharsets.UTF_8);
    }

    @Override
    public FilterResponse<?> getAllUser(UserFilterRequest filterRequest) {

        FilterResponse<UserResponse> filterResponse = FilterResponse.<UserResponse>builder()
                .pageNumber(filterRequest.getPage())
                .pageSize(filterRequest.getSize())
                .offset((long) (filterRequest.getPage() - 1) * filterRequest.getSize())
                .build();

        BooleanExpression wherePattern = QUserEntity.userEntity.role.roleName.notEqualsIgnoreCase(RoleEnum.ADMIN.name());

        if (filterRequest.getEnable() != null)
            wherePattern.and(QUserEntity.userEntity.enabled.eq(filterRequest.getEnable()));

        long totalElements = Optional.ofNullable(
                                                queryFactory
                                                .select(QUserEntity.userEntity.count())
                                                .from(QUserEntity.userEntity)
                                                .where(wherePattern)
                                                .fetchOne()
                                        ).orElse(0L);

        long totalPages = (long) Math.ceil((double) totalElements / filterResponse.getPageSize());
        filterResponse.setTotalElements(totalElements);
        filterResponse.setTotalPages(totalPages);

        OrderSpecifier<?> orderSpecifier;

        if (Sort.Direction.DESC.equals(filterRequest.getSortDirection()))
            orderSpecifier = QUserEntity.userEntity.updateAt.desc();
        else
            orderSpecifier = QUserEntity.userEntity.updateAt.asc();

        JPAQuery<UserEntity> query = queryFactory
                .selectFrom(QUserEntity.userEntity)
                .where(wherePattern)
                .orderBy(orderSpecifier)
                .offset(filterResponse.getOffset())
                .limit(filterResponse.getPageSize());

        filterResponse.setContent(
                UserMapper.INSTANCE.toUserResponseList(query.fetch())
        );

        return filterResponse;
    }

    @Transactional
    @Override
    public void enableUser(UUID userId, Boolean enable) {

        if(enable == null)
            throw new BadRequestException("The enable parameter is required");

        UserEntity user = findUserById(userId);

        user.setEnabled(enable);
        userRepository.save(user);

        if (enable)
            MessageResponseHolder.setMessage("Enable account of UserEntity successfully");
        else
            MessageResponseHolder.setMessage("Disable account of UserEntity successfully");

    }

    @Override
    public List<CountMockTestTopicResponse> getCountMockTestOfTopic(String date, UUID packId) {

        PackEntity pack = packService.getPackById(packId);

        List<TopicEntity> listTopic = topicService.getAllTopicToPack(pack);

        return listTopic.stream().map(topic -> {

            List<MockTestEntity> mockTests;

            if(date == null) mockTests = mockTestService.getAllMockTestToTopic(topic);
            else{
                String[] str = date.split("-");
                String day = null, year, month = null;
                year = str[0];

                if (str.length > 1) {
                    month = str[1];
                    if (str.length > 2) {
                        day = str[2];
                    }
                }

                mockTests = mockTestService.getAllMockTestByYearMonthAndDay(topic, year, month, day);
            }

            return CountMockTestTopicResponse.builder()
                    .topicName(topic.getTopicName())
                    .countMockTest(mockTests.size())
                    .build();
        }).toList();
    }
}
