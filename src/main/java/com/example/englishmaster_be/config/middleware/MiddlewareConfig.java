package com.example.englishmaster_be.config.middleware;

import com.example.englishmaster_be.advice.exception.GlobalExceptionHandler;
import com.example.englishmaster_be.advice.exception.ApplicationException;
import com.example.englishmaster_be.domain.user.auth.service.jwt.JwtService;
import com.example.englishmaster_be.domain.user.auth.service.invalid_token.IInvalidTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Slf4j(topic = "MIDDLEWARE-FILTER")
@Configuration
public class MiddlewareConfig extends OncePerRequestFilter {

    private final JwtService jwtUtil;

    private final UserDetailsService userDetailsService;

    private final IInvalidTokenService invalidTokenService;

    private final GlobalExceptionHandler globalExceptionHandler;

    public MiddlewareConfig(JwtService jwtUtil, UserDetailsService userDetailsService, IInvalidTokenService invalidTokenService, GlobalExceptionHandler globalExceptionHandler) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.invalidTokenService = invalidTokenService;
        this.globalExceptionHandler = globalExceptionHandler;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws IOException {
        try{
            String headerAuth = request.getHeader("Authorization");
            if (headerAuth != null && !headerAuth.isEmpty()){
                String prefixHeaderAuth = "Bearer";
                String jwtToken = headerAuth.substring(prefixHeaderAuth.length()).trim();
                if (!jwtUtil.isValidToken(jwtToken) || invalidTokenService.inValidToken(jwtToken))
                    throw new ApplicationException(HttpStatus.UNAUTHORIZED, "Invalid token", new Exception("token"));
                String username = jwtUtil.extractUsername(jwtToken);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if(!userDetails.isEnabled()) throw new ApplicationException(HttpStatus.UNAUTHORIZED, "Account disabled");
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, jwtToken, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
        }
        catch (Exception ex){
            logger.error("Cannot set user authentication: {}", ex);
            if(ex instanceof ApplicationException aex){
                globalExceptionHandler.printError(aex, HttpStatus.UNAUTHORIZED, request, response);
            }
        }
    }
}
