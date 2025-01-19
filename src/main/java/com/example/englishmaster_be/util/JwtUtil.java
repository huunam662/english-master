package com.example.englishmaster_be.util;

import com.example.englishmaster_be.common.constant.InvalidTokenTypeEnum;
import com.example.englishmaster_be.model.session_active.SessionActiveEntity;
import com.example.englishmaster_be.shared.invalid_token.service.InvalidTokenService;
import com.example.englishmaster_be.shared.session_active.service.SessionActiveService;
import com.example.englishmaster_be.value.JwtValue;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class JwtUtil {

    JwtValue jwtValue;

    SessionActiveService sessionActiveService;

    InvalidTokenService invalidTokenService;

    Boolean isTokenExpired(String token) {

        Date now = new Date(System.currentTimeMillis());

        Boolean isExpired = !now.before(extractExpiration(token));

        if(isExpired) {

            SessionActiveEntity sessionActive = sessionActiveService.getByToken(token);

            if(sessionActive != null) {

                invalidTokenService.insertInvalidToken(sessionActive, InvalidTokenTypeEnum.EXPIRED);
                sessionActiveService.deleteBySessionEntity(sessionActive);
            }
        }

        return isExpired;
    }

    Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    Key getSignKey(){
        return Keys.hmacShaKeyFor(jwtValue.getJwtSecret().getBytes(StandardCharsets.UTF_8));
    }

    Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(
                        getSignKey()
                ).build()
                .parseClaimsJws(token)
                .getBody();
    }

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    String buildScope(Collection<? extends GrantedAuthority> grantedAuthorities){

        return grantedAuthorities
                .stream()
                .iterator()
                .next()
                .getAuthority()
                .trim()
                .split("ROLE_")[1];
    }

    @SneakyThrows
    public String hashToHex(String token){

        Key key = Keys.hmacShaKeyFor(jwtValue.getSaltHashToken().getBytes(StandardCharsets.UTF_8));

        Mac mac = Mac.getInstance(key.getAlgorithm());

        mac.init(key);

        byte[] rawHmac = mac.doFinal(token.getBytes(StandardCharsets.UTF_8));

        return Hex.toHexString(rawHmac);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public LocalDateTime getTokenExpireFromJWT(String token){

        long expirationTimeMs = extractClaim(token, Claims::getExpiration).getTime();

        return Instant.ofEpochMilli(expirationTimeMs)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public Boolean isValidToken(String token) {

        try{

            return !isTokenExpired(token);

        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;


    }

    public String generateToken(UserDetails userDetails) {

        Map<String, Object> payload = new HashMap<>();
        payload.put("sub", userDetails.getUsername());
        payload.put("scp", buildScope(userDetails.getAuthorities()));

        return Jwts
                .builder()
                .setClaims(payload)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(
                        new Date(System.currentTimeMillis() + jwtValue.getJwtExpiration())
                )
                .signWith(
                        getSignKey(),
                        SignatureAlgorithm.HS512
                ).compact();
    }

}

