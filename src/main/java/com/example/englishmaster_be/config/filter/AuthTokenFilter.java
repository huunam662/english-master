package com.example.englishmaster_be.config.filter;

import com.example.englishmaster_be.common.constant.error.ErrorEnum;
import com.example.englishmaster_be.config.jwt.JwtUtil;
import com.example.englishmaster_be.common.dto.response.ExceptionResponseModel;
import com.example.englishmaster_be.exception.template.CustomException;
import com.example.englishmaster_be.shared.service.invalid_token.IInvalidTokenService;
import io.swagger.v3.core.util.Json;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthTokenFilter extends OncePerRequestFilter {

    JwtUtil jwtUtil;

    UserDetailsService userDetailsService;

    IInvalidTokenService invalidTokenService;


    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) {

        try{

            String headerAuth = request.getHeader("Authorization");

            if (headerAuth != null && !headerAuth.isEmpty()){

                String jwtToken = headerAuth.substring(7);

                String username = jwtUtil.extractUsername(jwtToken);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (!jwtUtil.validateToken(jwtToken, userDetails) || invalidTokenService.invalidToken(jwtToken))
                    throw new CustomException(ErrorEnum.UNAUTHENTICATED);

                if(!userDetails.isEnabled())
                    throw new CustomException(ErrorEnum.ACCOUNT_DISABLED);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);

            }

            filterChain.doFilter(request, response);
    
        }
        catch (Exception e){

            logger.error("Cannot set user authentication: {}", e);

            if(e instanceof CustomException customException) {
                writeExceptionBodyResponse(response, customException.getError());
                return;
            }

            writeExceptionBodyResponse(response, ErrorEnum.UNAUTHENTICATED);
        }
    }


    @SneakyThrows
    private void writeExceptionBodyResponse(HttpServletResponse response, ErrorEnum error){

        response.setStatus(error.getStatusCode().value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(
                Json.pretty(
                        ExceptionResponseModel.builder()
                                .success(Boolean.FALSE)
                                .status(error.getStatusCode())
                                .code(error.getStatusCode().value())
                                .message(error.getMessage())
                                .build()
                )
        );
    }
}
