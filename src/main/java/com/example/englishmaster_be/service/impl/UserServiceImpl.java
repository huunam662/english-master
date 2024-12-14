package com.example.englishmaster_be.service.impl;

import com.example.englishmaster_be.common.constaint.ConfirmRegisterTypeEnum;
import com.example.englishmaster_be.common.dto.response.FilterResponse;
import com.example.englishmaster_be.common.thread.MessageResponseHolder;
import com.example.englishmaster_be.config.jwt.JwtUtil;
import com.example.englishmaster_be.common.constaint.RoleEnum;
import com.example.englishmaster_be.exception.template.CustomException;
import com.example.englishmaster_be.mapper.ConfirmationTokenMapper;
import com.example.englishmaster_be.mapper.MockTestMapper;
import com.example.englishmaster_be.mapper.TopicMapper;
import com.example.englishmaster_be.model.request.*;
import com.example.englishmaster_be.model.request.ConfirmationToken.ConfirmationTokenRequest;
import com.example.englishmaster_be.model.request.User.ChangeProfileRequest;
import com.example.englishmaster_be.model.request.User.UserFilterRequest;
import com.example.englishmaster_be.common.constaint.error.ErrorEnum;
import com.example.englishmaster_be.exception.template.BadRequestException;
import com.example.englishmaster_be.mapper.UserMapper;
import com.example.englishmaster_be.model.response.*;
import com.example.englishmaster_be.repository.*;
import com.example.englishmaster_be.service.*;
import com.example.englishmaster_be.entity.*;
import com.example.englishmaster_be.value.LinkValue;
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
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    LinkValue linkValue;

    JPAQueryFactory queryFactory;

    JwtUtil jwtUtil;

    JavaMailSender mailSender;

    AuthenticationManager authenticationManager;

    PasswordEncoder passwordEncoder;

    ResourceLoader resourceLoader;

    OtpRepository otpRepository;

    RoleRepository roleRepository;

    UserRepository userRepository;

    ConfirmationTokenRepository confirmationTokenRepository;

    ContentRepository contentRepository;

    UserDetailsService userDetailsService;

    IInvalidTokenService iInvalidTokenService;

    IRefreshTokenService iRefreshTokenService;

    IOtpService otpService;

    ITopicService topicService;

    IUploadService uploadService;

    IMockTestService mockTestService;

    IPackService packService;

    IRefreshTokenService refreshTokenService;



    @Transactional
    @SneakyThrows
    @Override
    public void registerUser(UserRegisterRequest userRegisterRequest) {

        UserEntity user = userRepository.findByEmail(userRegisterRequest.getEmail()).orElse(null);

        if(user != null && user.getEnabled())
            throw new BadRequestException("Email đã được sử dụng");

        UserEntity userRegister = UserMapper.INSTANCE.toUserEntity(userRegisterRequest);
        userRegister.setUserId(user != null ? user.getUserId() : UUID.randomUUID());
        userRegister.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));
        userRegister.setRole(roleRepository.findByRoleName(RoleEnum.USER));
        userRegister.setEnabled(Boolean.FALSE);

        if(user != null && user.getConfirmToken() != null)
            userRegister.setConfirmToken(new ArrayList<>());

        userRegister = userRepository.save(userRegister);

        ConfirmationTokenResponse confirmationTokenResponse = this.createConfirmationToken(
                ConfirmationTokenRequest.builder().user(userRegister).build()
        );

        try {
            sendConfirmationEmail(userRegister.getEmail(), confirmationTokenResponse.getCode());
        } catch (IOException | MessagingException e) {
            throw new CustomException(ErrorEnum.SEND_EMAIL_FAILURE);
        }
    }

    protected void sendConfirmationEmail(String email, String confirmationToken) throws IOException, MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        String confirmationLink = linkValue.getLinkBE() + "api/user/register/confirm?token=" + confirmationToken;

        String templateContent = readTemplateContent("email_templates.html");
        templateContent = templateContent.replace("{{linkConfirm}}", confirmationLink)
                .replace("{{btnConfirm}}", "Xác nhận")
                .replace("{{nameLink}}", "Vui lòng chọn xác nhận để tiến hành đăng ký tài khoản.")
                .replace("*|current_year|*", String.valueOf(LocalDateTime.now().getYear()));

        helper.setTo(email);
        helper.setSubject("Xác nhận tài khoản");
        helper.setText(templateContent, true);
        mailSender.send(message);
    }

    @Transactional
    @Override
    public ConfirmationTokenResponse createConfirmationToken(ConfirmationTokenRequest confirmationTokenRequest) {

        ConfirmationTokenEntity confirmationToken = ConfirmationTokenMapper.INSTANCE.toConfirmationTokenEntity(confirmationTokenRequest);

        confirmationToken.setType(ConfirmRegisterTypeEnum.ACTIVE);
        confirmationToken.setUser(confirmationTokenRequest.getUser());
        confirmationToken.setCode(UUID.randomUUID().toString());
        confirmationToken.setCreateAt(LocalDateTime.now());
        confirmationToken = confirmationTokenRepository.save(confirmationToken);

        return ConfirmationTokenMapper.INSTANCE.toConfirmationTokenResponse(confirmationToken);
    }

    @Transactional
    @Override
    public void confirmRegister(String confirmationToken) {

        ConfirmationTokenEntity confirmToken = confirmationTokenRepository.findByCodeAndType(confirmationToken, ConfirmRegisterTypeEnum.ACTIVE);

        if (confirmToken == null)
            throw new BadRequestException("Không tồn tại");

        if (confirmToken.getUser().getEnabled())
            throw new BadRequestException("Tài khoản đã được xác thực");

        if ((confirmToken.getCreateAt().plusMinutes(5)).isBefore(LocalDateTime.now()))
            throw new BadRequestException("Phiên xác thực đã hết hạn, vui lòng đăng ký lại");

        UserEntity user = confirmToken.getUser();

        user.setEnabled(Boolean.TRUE);

        userRepository.save(user);
    }

    @Transactional
    @Override
    public AuthResponse login(UserLoginRequest userLoginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLoginRequest.getEmail(), userLoginRequest.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if(!userDetails.isEnabled())
            throw new CustomException(ErrorEnum.ACCOUNT_DISABLED);

        String jwt = jwtUtil.generateToken(userDetails);

        UserEntity user = findUser(userDetails);

        refreshTokenService.deleteAllTokenExpired(user);
        confirmationTokenRepository.deleteByUserAndType(user, ConfirmRegisterTypeEnum.ACTIVE);

        ConfirmationTokenEntity refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());

        return AuthResponse.builder()
                .accessToken(jwt)
                .refreshToken(refreshToken.getCode())
                .build();
    }

    @Override
    public void sendMail(String recipientEmail) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(recipientEmail);
        helper.setSubject("There is a long time you missing sign in ?");
        helper.setSubject("Take your sign in and looking new features from MEU-English, let's go!");

        mailSender.send(message);
    }

    public void notifyInactiveUsers(){
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(10);

        List<UserEntity> inactiveUsers = userRepository.findUsersNotLoggedInSince(cutoffDate);

        for (UserEntity user : inactiveUsers) {
            try {
                sendMail(user.getEmail());
            }catch (MessagingException e){
                System.out.println("Failed to send email to: " + user.getEmail());
            }
        }
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

    protected void sendOtpToEmail(String email, String otp)
            throws MessagingException, IOException
    {

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
        String confirmationLink = linkValue.getLinkFE() + "/forgetPass/confirm?token=" + confirmationToken;

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
    public void changePassword(ChangePasswordRequest changePasswordRequest) {

        String otp = changePasswordRequest.getCode();
        String newPassword = changePasswordRequest.getNewPassword();
        String confirmPassword = changePasswordRequest.getConfirmNewPassword();

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

        ConfirmationTokenEntity confirmToken = confirmationTokenRepository.findByCodeAndType(token, ConfirmRegisterTypeEnum.RESET_PASSWORD);

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

        UserDetails userDetails = userDetailsService.loadUserByUsername(token.getUser().getEmail());

        String newToken = jwtUtil.generateToken(userDetails);

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
    public void changePass(ChangePasswordRequest changePasswordRequest) {

        UserEntity user = currentUser();

        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=])"
                + "(?=\\S+$).{8,20}$";

        Pattern pattern = Pattern.compile(regex);

        Matcher matcherOldPassword = pattern.matcher(changePasswordRequest.getOldPassword());
        Matcher matcherNewPassword = pattern.matcher(changePasswordRequest.getNewPassword());
        Matcher matcherConfirmPassword = pattern.matcher(changePasswordRequest.getConfirmNewPassword());

        if(!matcherOldPassword.matches())
            throw new BadRequestException("Old password must be contain at least 1 uppercase, 1 lowercase, 1 numeric, 1 special character and no spaces");

        if (!matcherNewPassword.matches())
            throw new BadRequestException("New password must be contain at least 1 uppercase, 1 lowercase, 1 numeric, 1 special character and no spaces");

        if (!matcherConfirmPassword.matches())
            throw new BadRequestException("The confirm password must be contain at least 1 uppercase, 1 lowercase, 1 numeric, 1 special character and no spaces");

        if (!changePasswordRequest.getConfirmNewPassword().equals(changePasswordRequest.getNewPassword()))
            throw new BadRequestException("Your new password doesn't match with your confirm password");

        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword()))
            throw new BadRequestException("Your old password doesn't correct");

        if (passwordEncoder.matches(changePasswordRequest.getNewPassword(), user.getPassword()))
            throw new BadRequestException("New password can't be the same as old password");

        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));

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
    public void logoutUserOf(UserLogoutRequest userLogoutRequest) {

        boolean logoutSuccessfully = logoutUser();

        if(!logoutSuccessfully)
            throw new AuthenticationServiceException("You aren't logged in");

        iInvalidTokenService.insertInvalidToken(userLogoutRequest.getAccessToken());

        String refreshToken = userLogoutRequest.getRefreshToken();

        iRefreshTokenService.deleteRefreshToken(refreshToken);

    }

    @Override
    public void sendMail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
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

        BooleanExpression wherePattern = QUserEntity.userEntity.role.roleName.eq(RoleEnum.ADMIN);

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

    @Override
    public List<UserEntity> getUsersNotLoggedInLast10Days() {

        // Tính thời gian cách đây 10 ngày
        LocalDateTime tenDaysAgo = LocalDateTime.now().minusDays(10);

        // Xây dựng điều kiện truy vấn
        BooleanExpression condition = QUserEntity.userEntity.lastLogin.before(tenDaysAgo);

        // Thực hiện truy vấn với QueryDSL
        JPAQuery<UserEntity> query = queryFactory
                .selectFrom(QUserEntity.userEntity)
                .where(condition);

        // Chuyển đổi sang DTO để trả về
        return query.fetch();
    }

    @Transactional
    @Override
    public List<UserEntity> findUsersInactiveForDays(int inactiveDays) {

        // Tính toán ngày đã qua kể từ ngày hiện tại
        LocalDateTime thresholdDate = LocalDateTime.now().minusDays(inactiveDays);

        // Xây dựng điều kiện lọc
        BooleanExpression wherePattern = QUserEntity.userEntity.lastLogin.before(thresholdDate);

        // Truy vấn người dùng lâu ngày chưa đăng nhập
        JPAQuery<UserEntity> query = queryFactory
                .selectFrom(QUserEntity.userEntity)
                .where(wherePattern);

        // Chuyển đổi kết quả thành UserResponse
        return query.fetch();
    }

    @Transactional
    @Override
    public List<UserEntity> findUsersInactiveForDaysAndNotify(int inactiveDays) {

        LocalDateTime thresholdDate = LocalDateTime.now().minusDays(inactiveDays);

        BooleanExpression wherePattern = QUserEntity.userEntity.lastLogin.before(thresholdDate);

        JPAQuery<UserEntity> query = queryFactory
                .selectFrom(QUserEntity.userEntity)
                .where(wherePattern);

        List<UserEntity> inactiveUsers = query.fetch();

        for (UserEntity user : inactiveUsers)
            sendNotificationEmail(user);

        return inactiveUsers;
    }

    @Override
    public void sendNotificationEmail(UserEntity user){
        String subject = "Đã lâu bạn không truy cập rồi!";
        String body = String.format(
                "Hello %s, We from MEU-English." +
                        "\n\nFor a long time you missing for a sign in." +
                        "\n\nTake your come bank to looking for a new feature from us." +
                        "\n\nBest regards," +
                        "\nOur team.",
                user.getName()
        );
        sendMail(user.getEmail(),subject,body);
    }
}
