package com.example.englishmaster_be.Configuration.jwt;

import com.example.englishmaster_be.Model.CustomUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.xml.bind.DatatypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    @Value("${masterE.jwtSecret}")
    private String jwtSecret;

    @Value("${masterE.jwtExpiration}")
    private int jwtExpirationMs;

    public String generateJwtToken(CustomUserDetails userPrincipal) {

        return generateTokenFromUsername(userPrincipal.getUsername());

//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//
//        return Jwts.builder()
//                .setSubject((userDetails.getUsername()))
//                .setIssuedAt(new Date())
//                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
//                .signWith(SignatureAlgorithm.HS256, key())
//                .compact();
    }

    public String generateTokenFromUsername(String username) {
        return Jwts.builder().setSubject(username).setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)).signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

//    private Key key() {
////        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
//        return Keys.secretKeyFor(SignatureAlgorithm.HS256);
//    }
//
//    public String getUserNameFromJwtToken(String token) {
//        return Jwts.parserBuilder().setSigningKey(key()).build()
//                .parseClaimsJws(token).getBody().getSubject();
//    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecret).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}
