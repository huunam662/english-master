package com.example.englishmaster_be.domain.user.user.service;

import com.example.englishmaster_be.advice.exception.ApplicationException;
import com.example.englishmaster_be.common.constant.InvalidTokenType;
import com.example.englishmaster_be.common.constant.Role;
import com.example.englishmaster_be.common.constant.SessionActiveType;
import com.example.englishmaster_be.domain.user.auth.dto.res.UserAuthRes;
import com.example.englishmaster_be.domain.user.auth.model.SessionActiveEntity;
import com.example.englishmaster_be.domain.excel.util.ExcelUtil;
import com.example.englishmaster_be.domain.upload.meu.dto.req.FileDeleteReq;
import com.example.englishmaster_be.domain.upload.meu.service.IUploadService;
import com.example.englishmaster_be.domain.user.user.dto.req.UserChangeProfileReq;
import com.example.englishmaster_be.domain.user.user.mapper.UserMapper;
import com.example.englishmaster_be.domain.user.user.model.RoleEntity;
import com.example.englishmaster_be.domain.user.user.model.UserEntity;
import com.example.englishmaster_be.domain.user.user.repository.UserJdbcRepository;
import com.example.englishmaster_be.domain.user.user.repository.RoleRepository;
import com.example.englishmaster_be.domain.user.user.repository.UserRepository;
import com.example.englishmaster_be.domain.user.auth.service.invalid_token.IInvalidTokenService;
import com.example.englishmaster_be.domain.user.auth.service.jwt.JwtService;
import com.example.englishmaster_be.domain.user.auth.service.session_active.ISessionActiveService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


@Slf4j(topic = "USER-SERVICE")
@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final IUploadService uploadService;
    private final PasswordEncoder passwordEncoder;
    private final ISessionActiveService sessionActiveService;
    private final IInvalidTokenService invalidTokenService;
    private final UserJdbcRepository userJdbcRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;

    @Lazy
    public UserService(UserRepository userRepository, IUploadService uploadService, PasswordEncoder passwordEncoder, ISessionActiveService sessionActiveService, IInvalidTokenService invalidTokenService, UserJdbcRepository userJdbcRepository, RoleRepository roleRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.uploadService = uploadService;
        this.passwordEncoder = passwordEncoder;
        this.sessionActiveService = sessionActiveService;
        this.invalidTokenService = invalidTokenService;
        this.userJdbcRepository = userJdbcRepository;
        this.roleRepository = roleRepository;
        this.jwtService = jwtService;
    }

    @Transactional
    @Override
    @SneakyThrows
    public UserEntity changeProfile(UserChangeProfileReq changeProfileRequest) {

        UserEntity user = currentUser();

        UserMapper.INSTANCE.flowToUserEntity(changeProfileRequest, user);

        if(changeProfileRequest.getAvatar() != null && !changeProfileRequest.getAvatar().isEmpty()){

            if(user.getAvatar() != null && !user.getAvatar().isEmpty())
                CompletableFuture.runAsync(() -> {
                    try {
                        uploadService.delete(new FileDeleteReq(user.getAvatar()));
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });

            user.setAvatar(changeProfileRequest.getAvatar());
        }

        return userRepository.save(user);
    }

    @Transactional
    @Override
    public UserEntity saveUser(UserEntity user) {

        return userRepository.save(user);
    }


    @Override
    public UserEntity getUserById(UUID userId) {

        return userRepository.findById(userId).orElseThrow(
                () -> new ApplicationException(HttpStatus.BAD_REQUEST, "User not existed.")
        );
    }

    @Override
    public UserEntity currentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails)
            return (UserEntity) userDetails;

        throw new ApplicationException(HttpStatus.UNAUTHORIZED, "Please authenticate first.");
    }

    @Override
    public Boolean currentUserIsAdmin(UserDetails userDetails) {

        if(userDetails == null)
            userDetails = currentUser();

        UserEntity currentUser = (UserEntity) userDetails;

        return currentUser.getRole().getRoleName().equals(Role.ADMIN);
    }

    @Override
    public UserEntity getUserByEmail(String email) {

        return userRepository.findByEmail(email).orElseThrow(
                () -> new ApplicationException(HttpStatus.BAD_REQUEST, "User not existed.", new Exception("email"))
        );
    }

    @Override
    public UserEntity getUserByEmail(String email, Boolean throwable) {

        if(throwable) return getUserByEmail(email);

        return userRepository.findByEmail(email).orElse(null);
    }

    @Transactional
    @Override
    public void enabledUser(UUID userId) {

        userRepository.updateIsEnabled(userId);
    }

    @Transactional
    @Override
    public void updateLastLoginTime(UUID userId, LocalDateTime lastLoginTime) {

        if(lastLoginTime == null) lastLoginTime = LocalDateTime.now();

        userRepository.updateLastLogin(userId, lastLoginTime);
    }


    @Transactional
    @Override
    public UserAuthRes updatePassword(UserEntity user, String oldPassword, String newPassword) {

        if(!passwordEncoder.matches(oldPassword, user.getPassword()))
            throw new ApplicationException(HttpStatus.UNAUTHORIZED, "Wrong old password.", new Exception("oldPassword"));

        updatePasswordForgot(user, newPassword);

        String jwtToken = jwtService.generateToken(user);

        SessionActiveEntity sessionActive = sessionActiveService.saveSessionActive(user, jwtToken);

        return UserMapper.INSTANCE.toUserAuthResponse(sessionActive.getCode(), jwtToken, user);
    }


    @Transactional
    @Override
    public void updatePasswordForgot(UserEntity user, String newPassword) {

//        if (passwordEncoder.matches(newPassword, user.getPassword()))
//            throw new ErrorHolder(Error.BAD_REQUEST, "New password mustn't match with old password.");

        newPassword = passwordEncoder.encode(newPassword);

        userRepository.updatePassword(newPassword, user.getEmail());

        List<SessionActiveEntity> sessionActiveEntityList = sessionActiveService.getSessionActiveList(user, SessionActiveType.REFRESH_TOKEN);

        invalidTokenService.saveInvalidTokenList(sessionActiveEntityList, user, InvalidTokenType.PASSWORD_CHANGE);

        sessionActiveService.deleteAll(sessionActiveEntityList);
    }

    @Override
    public boolean isExistingEmail(String email) {

        return userRepository.existsByEmail(email);
    }

    @Transactional
    @Override
    public void saveAllUsersToExcel(MultipartFile file) {
        try(Workbook workbook = new XSSFWorkbook(file.getInputStream())){
            Sheet sheet = workbook.getSheetAt(0);
            List<UserEntity> usersToSave = new ArrayList<>();
            RoleEntity roleUser = roleRepository.findByRoleName(Role.USER);
            if(roleUser == null){
                roleUser = new RoleEntity();
                roleUser.setRoleName(Role.USER);
                roleUser = roleRepository.save(roleUser);
            }
            int next = 1;
            Row rowNext = sheet.getRow(next);
            while(rowNext != null){
                Cell cellEmail = rowNext.getCell(1);
                String contentEmail = ExcelUtil.getStringCellValue(cellEmail);
                Cell cellPassword = rowNext.getCell(2);
                String contentPassword = ExcelUtil.getStringCellValue(cellPassword);
                if(contentEmail == null || contentEmail.isEmpty() || contentPassword == null || contentPassword.isEmpty()){
                    rowNext = sheet.getRow(++next);
                    continue;
                }
                Cell cellFullName = rowNext.getCell(3);
                String fullName = ExcelUtil.getStringCellValue(cellFullName);
                Cell cellPhoneNumber = rowNext.getCell(4);
                String phoneNumber = ExcelUtil.getStringCellValue(cellPhoneNumber);
                UserEntity user =  new UserEntity();
                user.setEmail(contentEmail);
                user.setPassword(passwordEncoder.encode(contentPassword));
                user.setName(fullName);
                user.setPhone(phoneNumber);
                user.setRole(roleUser);
                user.setEnabled(true);
                usersToSave.add(user);
                rowNext = sheet.getRow(++next);
            }
            List<String> emails = usersToSave.stream().map(UserEntity::getEmail).toList();
            List<String> userEmails = userRepository.findAllEmailIn(emails);
            usersToSave = usersToSave.stream().filter(user -> !userEmails.contains(user.getEmail())).toList();
            userJdbcRepository.batchInsertUsers(usersToSave);
        }
        catch (Exception e){
            log.error(e.getMessage());
        }
    }
}
