package com.example.englishmaster_be.domain.user.service;

import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.common.constant.InvalidTokenType;
import com.example.englishmaster_be.common.constant.Role;
import com.example.englishmaster_be.common.constant.SessionActiveType;
import com.example.englishmaster_be.common.constant.error.Error;
import com.example.englishmaster_be.domain.auth.dto.response.UserAuthResponse;
import com.example.englishmaster_be.domain.auth.model.SessionActiveEntity;
import com.example.englishmaster_be.domain.excel.util.ExcelUtil;
import com.example.englishmaster_be.domain.upload.meu.request.FileDeleteRequest;
import com.example.englishmaster_be.domain.upload.meu.service.IUploadService;
import com.example.englishmaster_be.domain.user.dto.request.*;
import com.example.englishmaster_be.domain.user.mapper.UserMapper;
import com.example.englishmaster_be.domain.user.model.RoleEntity;
import com.example.englishmaster_be.domain.user.model.UserEntity;
import com.example.englishmaster_be.domain.user.repository.jdbc.UserJdbcRepository;
import com.example.englishmaster_be.domain.user.repository.jpa.RoleRepository;
import com.example.englishmaster_be.domain.user.repository.jpa.UserRepository;
import com.example.englishmaster_be.domain.auth.service.invalid_token.IInvalidTokenService;
import com.example.englishmaster_be.domain.auth.service.jwt.JwtService;
import com.example.englishmaster_be.domain.auth.service.session_active.ISessionActiveService;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService implements IUserService, UserDetailsService {

    UserRepository userRepository;

    IUploadService uploadService;

    PasswordEncoder passwordEncoder;

    ISessionActiveService sessionActiveService;

    IInvalidTokenService invalidTokenService;

    UserJdbcRepository userJdbcRepository;

    RoleRepository roleRepository;

    JwtService jwtService;


    @Transactional
    @Override
    @SneakyThrows
    public UserEntity changeProfile(UserChangeProfileReq changeProfileRequest) {

        UserEntity user = currentUser();

        UserMapper.INSTANCE.flowToUserEntity(changeProfileRequest, user);

        if(changeProfileRequest.getAvatar() != null && !changeProfileRequest.getAvatar().isEmpty()){

            if(user.getAvatar() != null && !user.getAvatar().isEmpty())
                uploadService.delete(
                        FileDeleteRequest.builder()
                                .filepath(user.getAvatar())
                                .build()
                );

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
                () -> new ErrorHolder(Error.BAD_REQUEST, "User not existed.")
        );
    }

    @Override
    public UserEntity currentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails)
            return (UserEntity) userDetails;

        throw new AuthenticationServiceException("Please authenticate first.");
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
                () -> new ErrorHolder(Error.BAD_REQUEST, "User not existed.")
        );
    }

    @Override
    public UserEntity getUserByEmail(String email, Boolean throwable) {

        if(throwable) return getUserByEmail(email);

        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return userRepository.findUserJoinRoleByEmail(username).orElseThrow(
                () -> new ErrorHolder(Error.BAD_CREDENTIALS)
        );
    }

    @Transactional
    @Override
    public void enabledUser(UUID userId) {

        userRepository.updateIsEnabled(userId);
    }

    @Transactional
    @Override
    public void updateLastLoginTime(UUID userId, LocalDateTime lastLoginTime) {

        if(userId == null) throw new ErrorHolder(Error.SERVER_ERROR);

        if(lastLoginTime == null) lastLoginTime = LocalDateTime.now();

        userRepository.updateLastLogin(userId, lastLoginTime);
    }


    @Transactional
    @Override
    public UserAuthResponse updatePassword(UserEntity user, String oldPassword, String newPassword) {

        if(!passwordEncoder.matches(oldPassword, user.getPassword()))
            throw new ErrorHolder(Error.BAD_CREDENTIALS, "Wrong old password.");

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
        Assert.notNull(file, "File excel for all users is required.");
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
