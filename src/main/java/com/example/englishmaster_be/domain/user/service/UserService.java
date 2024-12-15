package com.example.englishmaster_be.domain.user.service;

import com.example.englishmaster_be.common.constant.ConfirmRegisterTypeEnum;
import com.example.englishmaster_be.common.dto.response.FilterResponse;
import com.example.englishmaster_be.config.jwt.JwtUtil;
import com.example.englishmaster_be.common.constant.RoleEnum;
import com.example.englishmaster_be.domain.exam.dto.response.ExamResultResponse;
import com.example.englishmaster_be.domain.mock_test.service.IMockTestService;
import com.example.englishmaster_be.domain.pack.service.IPackService;
import com.example.englishmaster_be.domain.topic.service.ITopicService;
import com.example.englishmaster_be.domain.upload.service.IUploadService;
import com.example.englishmaster_be.domain.user.dto.request.*;
import com.example.englishmaster_be.domain.user.dto.response.UserAuthResponse;
import com.example.englishmaster_be.exception.template.CustomException;
import com.example.englishmaster_be.mapper.ConfirmationTokenMapper;
import com.example.englishmaster_be.mapper.MockTestMapper;
import com.example.englishmaster_be.mapper.TopicMapper;
import com.example.englishmaster_be.domain.user.dto.request.UserConfirmTokenRequest;
import com.example.englishmaster_be.common.constant.error.ErrorEnum;
import com.example.englishmaster_be.exception.template.BadRequestException;
import com.example.englishmaster_be.mapper.UserMapper;
import com.example.englishmaster_be.model.confirmation_token.ConfirmationTokenEntity;
import com.example.englishmaster_be.model.confirmation_token.ConfirmationTokenRepository;
import com.example.englishmaster_be.model.content.ContentRepository;
import com.example.englishmaster_be.model.mock_test.MockTestEntity;
import com.example.englishmaster_be.model.mock_test.QMockTestEntity;
import com.example.englishmaster_be.model.otp.OtpEntity;
import com.example.englishmaster_be.model.otp.OtpRepository;
import com.example.englishmaster_be.model.role.RoleRepository;
import com.example.englishmaster_be.model.topic.QTopicEntity;
import com.example.englishmaster_be.model.topic.TopicEntity;
import com.example.englishmaster_be.model.user.QUserEntity;
import com.example.englishmaster_be.model.user.UserEntity;
import com.example.englishmaster_be.model.user.UserRepository;
import com.example.englishmaster_be.domain.user.dto.response.UserConfirmTokenResponse;
import com.example.englishmaster_be.shared.service.invalid_token.IInvalidTokenService;
import com.example.englishmaster_be.shared.service.otp.IOtpService;
import com.example.englishmaster_be.shared.service.refreshToken.IRefreshTokenService;
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
public class UserService implements IUserService {

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

        UserConfirmTokenResponse confirmationTokenResponse = this.createConfirmationToken(
                UserConfirmTokenRequest.builder().user(userRegister).build()
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
        String confirmationLink = linkValue.getLinkFE() + "register/confirm?token=" + confirmationToken;

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
    public UserConfirmTokenResponse createConfirmationToken(UserConfirmTokenRequest confirmationTokenRequest) {

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
    public UserAuthResponse login(UserLoginRequest userLoginRequest) {

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

        return UserAuthResponse.builder()
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
    public void changePassword(UserChangePasswordRequest changePasswordRequest) {

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
    public UserAuthResponse refreshToken(UserRefreshTokenRequest refreshTokenDTO) {

        String refresh = refreshTokenDTO.getRequestRefresh();

        ConfirmationTokenEntity token = refreshTokenService.findByToken(refresh);

        if (token == null)
            throw new BadRequestException("Refresh token isn't existed");

        refreshTokenService.verifyExpiration(token);

        UserDetails userDetails = userDetailsService.loadUserByUsername(token.getUser().getEmail());

        String newToken = jwtUtil.generateToken(userDetails);

        return UserAuthResponse.builder()
                .accessToken(newToken)
                .build();
    }

    @Transactional
    @Override
    public UserEntity changeProfile(UserChangeProfileRequest changeProfileRequest) {

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
    public void changePass(UserChangePasswordRequest changePasswordRequest) {

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
