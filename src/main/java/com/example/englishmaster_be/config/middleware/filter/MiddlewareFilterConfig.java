package com.example.englishmaster_be.config.middleware.filter;

import com.example.englishmaster_be.advice.exception.handler.GlobalExceptionHandler;
import com.example.englishmaster_be.common.constant.error.Error;
import com.example.englishmaster_be.shared.service.jwt.JwtService;
import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
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

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j(topic = "MIDDLEWARE-FILTER")
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MiddlewareFilterConfig extends OncePerRequestFilter {

    JwtService jwtUtil;

    UserDetailsService userDetailsService;

    IInvalidTokenService invalidTokenService;

    GlobalExceptionHandler globalExceptionHandler;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws IOException {

        log.info("-> doFilterInternal");

        try{

            String headerAuth = request.getHeader("Authorization");

            if (headerAuth != null && !headerAuth.isEmpty()){

                String prefixHeaderAuth = "Bearer";

                String jwtToken = headerAuth.substring(prefixHeaderAuth.length()).trim();

                if (!jwtUtil.isValidToken(jwtToken) || invalidTokenService.inValidToken(jwtToken))
                    throw new ErrorHolder(Error.UNAUTHENTICATED);

                String username = jwtUtil.extractUsername(jwtToken);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if(!userDetails.isEnabled())
                    throw new ErrorHolder(Error.ACCOUNT_DISABLED);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);

            }

            filterChain.doFilter(request, response);

        }
        catch (Exception e){

            logger.error("Cannot set user authentication: {}", e);

            Error error = Error.UNAUTHENTICATED;

            if(e instanceof ErrorHolder errorHolder)
                error = errorHolder.getError();

            log.error("{} -> code {}", e, error.getStatusCode());

            globalExceptionHandler.printError(error, response);
        }
    }
}
