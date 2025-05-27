package com.example.englishmaster_be.shared.service.jwt;

import com.example.englishmaster_be.common.constant.InvalidTokenType;
import com.example.englishmaster_be.model.session_active.SessionActiveEntity;
import com.example.englishmaster_be.shared.service.invalid_token.InvalidTokenService;
import com.example.englishmaster_be.shared.service.session_active.SessionActiveService;
import com.example.englishmaster_be.value.JwtValue;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
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
@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtService {

    JwtValue jwtValue;

    SessionActiveService sessionActiveService;

    Boolean isTokenExpired(String token) {

        Date now = new Date(System.currentTimeMillis());

        Boolean isExpired = !now.before(extractExpiration(token));

        if(isExpired) sessionActiveService.deleteByToken(token);

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

