package com.example.englishmaster_be.config.jwt;

import com.example.englishmaster_be.value.JwtValue;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
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

    Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
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

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public LocalDateTime getTokenExpireFromJWT(String token){

        long expirationTime  = extractClaim(token, Claims::getExpiration).getTime();

        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(expirationTime), ZoneId.systemDefault()
        );
    }

    public Boolean validateToken(String token, UserDetails userDetails) {

        try{

            final String tokenExtracted = extractUsername(token);
            return (tokenExtracted.equals(userDetails.getUsername()) && !isTokenExpired(token));
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
        payload.put("iat", new Date());
        payload.put("exp", Date.from(
                Instant.now()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .plusMillis(jwtValue.getJwtExpiration())
        ));

        return Jwts
                .builder()
                .setClaims(payload)
                .signWith(
                        getSignKey(),
                        SignatureAlgorithm.HS512
                ).compact();
    }

}

