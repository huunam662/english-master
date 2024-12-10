package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Common.dto.response.FilterResponse;
import com.example.englishmaster_be.Configuration.global.thread.MessageResponseHolder;
import com.example.englishmaster_be.Configuration.jwt.JwtUtils;
import com.example.englishmaster_be.Common.enums.RoleEnum;
import com.example.englishmaster_be.DTO.*;
import com.example.englishmaster_be.DTO.ConfirmationToken.SaveConfirmationTokenDTO;
import com.example.englishmaster_be.DTO.User.ChangePassDTO;
import com.example.englishmaster_be.DTO.User.ChangeProfileDTO;
import com.example.englishmaster_be.DTO.User.UserFilterRequest;
import com.example.englishmaster_be.Exception.Error;
import com.example.englishmaster_be.Exception.Response.BadRequestException;
import com.example.englishmaster_be.Exception.Response.ResponseNotFoundException;
import com.example.englishmaster_be.Mapper.UserMapper;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.*;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.*;
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
import java.util.stream.Collectors;

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
    @Override
    public void registerUser(UserRegisterDTO userRegisterDTO) {

        User user = userRepository.findByEmail(userRegisterDTO.getEmail()).orElse(null);

        if (user != null && !user.isEnabled())
            userRepository.delete(user);

        user = UserMapper.INSTANCE.toEntity(userRegisterDTO);
        user.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
        user.setRole(roleRepository.findByRoleName(RoleEnum.USER.name()));

        user = userRepository.save(user);

        ConfirmationTokenResponse confirmationTokenResponse = confirmationTokenService.createConfirmationToken(
                SaveConfirmationTokenDTO.builder().user(user).build()
        );

        try {
            sendConfirmationEmail(user.getEmail(), confirmationTokenResponse.getCode());
        } catch (IOException | MessagingException e) {
            throw new ResponseNotFoundException("Failed to send confirmation email");
        }
    }

    @Transactional
    @Override
    public void confirmRegister(String confirmationToken) {

        ConfirmationToken confirmToken = confirmationTokenRepository.findByCodeAndType(confirmationToken, "ACTIVE");

        if (confirmToken == null)
            throw new BadRequestException("Invalid verification code");

        if (confirmToken.getUser().isEnabled())
            throw new BadRequestException("Account has been verified");

        if ((confirmToken.getCreateAt().plusMinutes(5)).isBefore(LocalDateTime.now()))
            throw new BadRequestException("Verification code has expired, Please register again");

        User user = confirmToken.getUser();

        user.setEnabled(Boolean.TRUE);

        userRepository.save(user);
    }

    @Override
    public AuthResponse login(UserLoginDTO userLoginDTO) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLoginDTO.getEmail(), userLoginDTO.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if(!userDetails.isEnabled())
            throw new DisabledException(Error.ACCOUNT_DISABLED.getMessage());

        String jwt = jwtUtils.generateJwtToken(userDetails);

        User user = findUser(userDetails);

        refreshTokenService.deleteAllTokenExpired(user);

        ConfirmationToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());

        return new AuthResponse(jwt, refreshToken.getCode());
    }

    @Override
    public void sendMail(String recipientEmail) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(recipientEmail);
        helper.setSubject("Đã lâu bạn không đăng nhập rồi nhỉ?");
        helper.setSubject("Đã lâu bạn không đăng nhập rồi nhỉ?. Hãy đăng nhập và khám phá những tính năng mới của MEU-English nào!");

        mailSender.send(message);
    }

    public void notifyInactiveUsers(){
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(10);

        List<User> inactiveUsers = userRepository.findUsersNotLoggedInSince(cutoffDate);

        for (User user : inactiveUsers) {
            try {
                sendMail(user.getEmail());
            }catch (MessagingException e){
                System.out.println("Failed to send email to: "+ user.getEmail());
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
    public void changePassword(ChangePasswordDTO changePasswordDTO) {

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

        ConfirmationToken confirmToken = confirmationTokenRepository.findByCodeAndType(token, "RESET_PASSWORD");

        if (confirmToken == null)
            throw new BadRequestException("Invalid reset code");

        if (confirmToken.getCreateAt().plusMinutes(5).isBefore(LocalDateTime.now()))
            throw new BadRequestException("Reset code has expired");

        return token;
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenDTO refreshTokenDTO) {

        String refresh = refreshTokenDTO.getRequestRefresh();

        ConfirmationToken token = refreshTokenService.findByToken(refresh);

        if (token == null)
            throw new BadRequestException("Refresh token isn't existed");

        refreshTokenService.verifyExpiration(token);

        String newToken = jwtUtils.generateTokenFromUsername(token.getUser().getEmail());

        return AuthResponse.builder()
                .accessToken(newToken)
                .build();
    }

    @Override
    public InformationUserResponse informationCurrentUser() {

        User currentUser = currentUser();

        return informationUserOf(currentUser);
    }

    @Override
    public InformationUserResponse informationUserOf(User user) {

        return InformationUserResponse.builder()
                .user(new UserResponse(user))
                .role(user.getRole().getRoleName())
                .build();
    }

    @Transactional
    @Override
    public InformationUserResponse changeProfile(ChangeProfileDTO changeProfileDTO) {

        User user = currentUser();

        if (changeProfileDTO.getAvatar() != null && !changeProfileDTO.getAvatar().isEmpty()) {
            if (user.getAvatar() != null && !user.getAvatar().isEmpty() && user.getAvatar().startsWith("https://s3.meu-solutions.com/"))
                contentRepository.deleteByContentData(user.getAvatar());

            String avatarPathResponse = uploadService.upload(changeProfileDTO.getAvatar(), "/", false, null, null);

            user.setAvatar(avatarPathResponse);
        }

        user.setName(changeProfileDTO.getName());
        user.setAddress(changeProfileDTO.getAddress());
        user.setPhone(changeProfileDTO.getPhone());

        user = userRepository.save(user);

        return informationUserOf(user);
    }

    @Transactional
    @Override
    public void changePass(ChangePassDTO changePassDTO) {

        User user = currentUser();

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

        User user = currentUser();

        JPAQuery<Topic> queryMockTest = queryFactory.select(QMockTest.mockTest.topic)
                                                    .from(QMockTest.mockTest)
                                                    .where(QMockTest.mockTest.user.userId.eq(user.getUserId()))
                                                    .groupBy(QMockTest.mockTest.topic);

        List<Topic> listTopicUser = queryMockTest.fetch();

        BooleanExpression wherePatternOfTopic = QTopic.topic.in(listTopicUser);

        long totalElements = Optional.ofNullable(
                queryFactory.select(QTopic.topic.count())
                            .from(QTopic.topic)
                            .where(wherePatternOfTopic)
                            .fetchOne()
                        ).orElse(0L);
        long totalPages = (long)Math.ceil((double) totalElements / filterResponse.getPageSize());
        filterResponse.setTotalElements(totalElements);
        filterResponse.setTotalPages(totalPages);
        filterResponse.withPreviousAndNextPage();

        OrderSpecifier<?> orderSpecifier;

        if (Sort.Direction.DESC.equals(filterRequest.getSortDirection()))
            orderSpecifier = QTopic.topic.updateAt.desc();
        else orderSpecifier = QTopic.topic.updateAt.asc();

        JPAQuery<Topic> query = queryFactory
                                .selectFrom(QTopic.topic)
                                .where(wherePatternOfTopic)
                                .orderBy(orderSpecifier)
                                .offset(filterResponse.getOffset())
                                .limit(filterResponse.getPageSize());

        filterResponse.setContent(
                query.fetch().stream().map(
                        topic -> {
                            ExamResultResponse examResultResponse = ExamResultResponse.builder()
                                    .topic(new TopicResponse(topic))
                                    .build();

                            JPAQuery<MockTest> queryListMockTest = queryFactory.selectFrom(QMockTest.mockTest)
                                    .where(QMockTest.mockTest.user.userId.eq(user.getUserId()))
                                    .where(QMockTest.mockTest.topic.eq(topic));

                            examResultResponse.setListMockTest(
                                    queryListMockTest.fetch().stream().map(MockTestResponse::new).toList()
                            );

                            return examResultResponse;
                        }
                ).toList()
        );

        return filterResponse;
    }

    @Override
    public FilterResponse<?> getInactiveUsers(UserFilterRequest filterRequest) {
        return null;
    }

    @Override
    public void logoutUserOf(UserLogoutDTO userLogoutDTO) {

        boolean logoutSuccessfully = logoutUser();

        if(!logoutSuccessfully)
            throw new AuthenticationServiceException("You aren't logged in");

        iInvalidTokenService.insertInvalidToken(userLogoutDTO.getAccessToken());

        String refreshToken = userLogoutDTO.getRefreshToken();

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

        User userEntity = findUserById(userId);

        userRepository.delete(userEntity);
    }


    @Override
    public User findUser(UserDetails userDetails) {
        return findUserByEmail(userDetails.getUsername());
    }

    @Override
    public User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails)
            return findUser(userDetails);

        throw new AuthenticationServiceException("Please authenticate user");
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new BadRequestException("User not found")
        );
    }

    @Override
    public User findUserById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("User not found")
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

        Otp otpRecord = otpRepository.findById(otp).orElse(null);

        if (otpRecord == null || !"Verified".equals(otpRecord.getStatus()))
            return false;

        User user = userRepository.findByEmail(otpRecord.getEmail()).orElse(null);

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

        BooleanExpression wherePattern = QUser.user.role.roleName.notEqualsIgnoreCase(RoleEnum.ADMIN.name());

        if (filterRequest.getEnable() != null)
            wherePattern.and(QUser.user.isEnabled.eq(filterRequest.getEnable()));

        long totalElements = Optional.ofNullable(
                                                queryFactory
                                                .select(QUser.user.count())
                                                .from(QUser.user)
                                                .where(wherePattern)
                                                .fetchOne()
                                        ).orElse(0L);

        long totalPages = (long) Math.ceil((double) totalElements / filterResponse.getPageSize());
        filterResponse.setTotalElements(totalElements);
        filterResponse.setTotalPages(totalPages);
        filterResponse.withPreviousAndNextPage();

        OrderSpecifier<?> orderSpecifier;

        if (Sort.Direction.DESC.equals(filterRequest.getSortDirection()))
            orderSpecifier = QUser.user.updateAt.desc();
        else
            orderSpecifier = QUser.user.updateAt.asc();

        JPAQuery<User> query = queryFactory
                .selectFrom(QUser.user)
                .where(wherePattern)
                .orderBy(orderSpecifier)
                .offset(filterResponse.getOffset())
                .limit(filterResponse.getPageSize());

        filterResponse.setContent(
                query.fetch().stream().map(UserResponse::new).toList()
        );

        return filterResponse;
    }

    @Transactional
    @Override
    public void enableUser(UUID userId, Boolean enable) {

        if(enable == null)
            throw new BadRequestException("The enable parameter is required");

        User user = findUserById(userId);

        user.setEnabled(enable);
        userRepository.save(user);

        if (enable)
            MessageResponseHolder.setMessage("Enable account of User successfully");
        else
            MessageResponseHolder.setMessage("Disable account of User successfully");

    }

    @Override
    public List<CountMockTestTopicResponse> getCountMockTestOfTopic(String date, UUID packId) {

        Pack pack = packService.findPackById(packId);

        List<Topic> listTopic = topicService.getAllTopicToPack(pack);

        return listTopic.stream().map(topic -> {

            List<MockTest> mockTests;

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

    public List<UserResponse> getUsersNotLoggedInLast10Days() {
        // Tính thời gian cách đây 10 ngày
        LocalDateTime tenDaysAgo = LocalDateTime.now().minusDays(10);

        // Xây dựng điều kiện truy vấn
        BooleanExpression condition = QUser.user.lastLogin.before(tenDaysAgo);

        // Thực hiện truy vấn với QueryDSL
        List<User> users = queryFactory
                .selectFrom(QUser.user)
                .where(condition)
                .fetch();

        // Chuyển đổi sang DTO để trả về
        return users.stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }
    @Transactional
    public List<UserResponse> findUsersInactiveForDays(int inactiveDays) {

        // Tính toán ngày đã qua kể từ ngày hiện tại
        LocalDateTime thresholdDate = LocalDateTime.now().minusDays(inactiveDays);

        // Xây dựng điều kiện lọc
        BooleanExpression wherePattern = QUser.user.lastLogin.before(thresholdDate);

        // Truy vấn người dùng lâu ngày chưa đăng nhập
        JPAQuery<User> query = queryFactory
                .selectFrom(QUser.user)
                .where(wherePattern);

        List<User> inactiveUsers = query.fetch();

        // Chuyển đổi kết quả thành UserResponse
        return inactiveUsers.stream()
                .map(UserResponse::new)
                .toList();
    }

    @Transactional
    public List<UserResponse> findUsersInactiveForDaysAndNotify(int inactiveDays) {
        LocalDateTime thresholdDate = LocalDateTime.now().minusDays(inactiveDays);

        BooleanExpression wherePattern = QUser.user.lastLogin.before(thresholdDate);

        JPAQuery<User> query = queryFactory
                .selectFrom(QUser.user)
                .where(wherePattern);
        List<User> inactiveUsers = query.fetch();

        for (User user : inactiveUsers) {
            sendNotificationEmail(user);
        }
        return inactiveUsers.stream()
                .map(UserResponse::new)
                .toList();
    }

    public void sendNotificationEmail(User user){
        String subject = "Đã lâu bạn không truy cập rồi!";
        String body = String.format(
                "Chào %s, \n\n Đã lâu rồi bạn không đăng nhập vào hệ thống. Hãy quay lại để khám phá các tính năng mới nào!\n\nTrân trọng, \n Đội ngũ chúng tôi.",
                user.getName()
        );
        sendMail(user.getEmail(),subject,body);
    }
}
