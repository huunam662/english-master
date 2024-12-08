package com.example.englishmaster_be.Configuration.filter;

import com.example.englishmaster_be.Configuration.jwt.JwtUtils;
import com.example.englishmaster_be.Exception.Error;
import com.example.englishmaster_be.Model.Response.ExceptionResponseModel;
import com.example.englishmaster_be.Service.IInvalidTokenService;
import io.swagger.v3.core.util.Json;
import jakarta.mail.AuthenticationFailedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.DisabledException;
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

    JwtUtils jwtUtils;

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

                String jwt = headerAuth.substring(7);

                if (!jwtUtils.validateJwtToken(jwt) || invalidTokenService.invalidToken(jwt))
                    throw new AuthenticationServiceException(null);

                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if(!userDetails.isEnabled())
                    throw new DisabledException(null);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);

            }

            filterChain.doFilter(request, response);

        }
        catch (Exception e){

            logger.error("Cannot set user authentication: {}", e);

            if(e instanceof DisabledException) {
                writeExceptionBodyResponse(response, Error.ACCOUNT_DISABLED);
                return;
            }

            writeExceptionBodyResponse(response, Error.UNAUTHENTICATED);
        }
    }


    @SneakyThrows
    private void writeExceptionBodyResponse(HttpServletResponse response, Error error){

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
