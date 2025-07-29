package com.example.englishmaster_be.domain.user.auth.service.user;

import com.example.englishmaster_be.advice.exception.ApplicationException;
import com.example.englishmaster_be.advice.exception.GlobalExceptionHandler;
import com.example.englishmaster_be.advice.response.ResultApiRes;
import com.example.englishmaster_be.common.constant.Role;
import com.example.englishmaster_be.domain.user.auth.dto.res.UserAuthRes;
import com.example.englishmaster_be.domain.user.auth.model.SessionActiveEntity;
import com.example.englishmaster_be.domain.user.auth.service.jwt.JwtService;
import com.example.englishmaster_be.domain.user.auth.service.session_active.ISessionActiveService;
import com.example.englishmaster_be.domain.user.user.mapper.UserMapper;
import com.example.englishmaster_be.domain.user.user.model.UserEntity;
import com.example.englishmaster_be.domain.user.user.repository.RoleRepository;
import com.example.englishmaster_be.domain.user.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


@Slf4j(topic = "AUTH-USER-SERVICE")
@Service
public class AuthUserService implements UserDetailsService, AuthenticationSuccessHandler, OAuth2UserService<OidcUserRequest, OidcUser> {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OidcUserService oidcUserService;
    private final JwtService jwtService;
    private final ISessionActiveService sessionActiveService;
    private final GlobalExceptionHandler globalExceptionHandler;

    public AuthUserService(GlobalExceptionHandler globalExceptionHandler, UserRepository userRepository, RoleRepository roleRepository, JwtService jwtService, ISessionActiveService sessionActiveService) {
        this.globalExceptionHandler = globalExceptionHandler;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtService = jwtService;
        this.sessionActiveService = sessionActiveService;
        this.oidcUserService = new OidcUserService();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserJoinRoleByEmail(username).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "User not existed.", new Exception("username"))
        );
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        return oidcUserService.loadUser(userRequest);
    }

    @Transactional
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try{
            if(authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() instanceof AnonymousAuthenticationToken)
                throw new ApplicationException(HttpStatus.UNAUTHORIZED, "Authentication failed.");
            DefaultOidcUser oidcUser = (DefaultOidcUser) authentication.getPrincipal();
            if(!oidcUser.getEmailVerified()) throw new ApplicationException(HttpStatus.UNAUTHORIZED, "Email not verified.", new Exception("email"));
            OAuth2AuthenticationToken auth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
            UserEntity user = userRepository.findUserJoinRoleByEmail(oidcUser.getEmail()).orElseGet(() -> {
                UserEntity userNew = new UserEntity();
                userNew.setEmail(oidcUser.getEmail());
                userNew.setName(oidcUser.getFullName());
                userNew.setPhone(oidcUser.getPhoneNumber());
                userNew.setAvatar(oidcUser.getPicture());
                userNew.setRole(roleRepository.findByRoleName(Role.USER));
                userNew.setEnabled(true);
                userNew.setUserType(auth2AuthenticationToken.getAuthorizedClientRegistrationId());
                return userRepository.save(userNew);
            });

            String jwtToken = jwtService.generateToken(user);
            SessionActiveEntity sessionActive = sessionActiveService.saveSessionActive(user, jwtToken);
            UserAuthRes userAuthRes = UserMapper.INSTANCE.toUserAuthResponse(sessionActive.getCode(), jwtToken, user);

            HttpStatus statusRes = HttpStatus.OK;
            ResultApiRes res = new ResultApiRes();
            res.setSuccess(true);
            res.setMessage("Successful");
            res.setStatus(statusRes);
            res.setCode(statusRes.value());
            res.setPath(request.getRequestURI());
            res.setResponseData(userAuthRes);

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(res.getCode());
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write(new ObjectMapper().writeValueAsString(res));
        }
        catch (Exception ex){
            log.error(ex.getMessage());
            HttpStatus status = HttpStatus.UNAUTHORIZED;
            Exception exr = new Exception("Authenticate failed.");
            globalExceptionHandler.printError(exr, status, request, response);
        }
    }
}
