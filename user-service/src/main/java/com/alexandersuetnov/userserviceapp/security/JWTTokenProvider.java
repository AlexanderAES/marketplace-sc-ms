package com.alexandersuetnov.userserviceapp.security;

import com.alexandersuetnov.userserviceapp.model.User;
import io.jsonwebtoken.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Log4j2
public class JWTTokenProvider {
    public String generatedToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal(); // возвращает объект, поэтому cast
        Date now = new Date(System.currentTimeMillis()); // берем текущее время
        Date expiryDate = new Date(now.getTime() + SecurityConstants.EXPIRATION_TIME); // время действия токена сейчас + EXPIRATION_TIME

        String userId = Long.toString(user.getId());

        Map<String, Object> claimsMap = new HashMap<>(); //объект claimsMap будем передавать в json web token-е
        claimsMap.put("id", userId);
        claimsMap.put("username", user.getEmail());
        claimsMap.put("name", user.getName());
        claimsMap.put("phoneNumber", user.getPhoneNumber());

        return Jwts.builder()
                .setSubject(userId)
                .addClaims(claimsMap)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, SecurityConstants.SECRET)
                .compact();
    }
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(SecurityConstants.SECRET)
                    .parseClaimsJws(token);
            return true;
        } catch (SignatureException |
                MalformedJwtException |
                ExpiredJwtException |
                UnsupportedJwtException |
                IllegalArgumentException ex) {
            log.error(ex.getMessage());
            return false;
        }
    }
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SecurityConstants.SECRET)
                .parseClaimsJws(token)
                .getBody();
        String id = (String) claims.get("id");
        return Long.parseLong(id);
    }

}
