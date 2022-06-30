package com.alexandersuetnov.imageservice.config;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

@RefreshScope
@Component
@RequiredArgsConstructor
@Log4j2
public class JWTFilter {

    public String getUserIdFromRequest(HttpServletRequest httpServletRequest) {
        String jwt = getJWTFromRequest(httpServletRequest);
        if (StringUtils.hasText(jwt) && validateToken(jwt)) {
            log.info("Get user id from token");
            return getUserIdFromToken(jwt);
        }
        log.error("Couldn't extract user id from token");
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(SecurityConstants.SECRET)
                    .parseClaimsJws(token);
            return true;
        } catch (
                MalformedJwtException |
                        ExpiredJwtException |
                        UnsupportedJwtException |
                        IllegalArgumentException ex) {
            log.error(ex.getMessage());
            return false;
        }
    }


    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SecurityConstants.SECRET)
                .parseClaimsJws(token)
                .getBody();
        String id = (String) claims.get("id");
        return id;
    }


    private String getJWTFromRequest(HttpServletRequest request) {
        String bearToken = request.getHeader(SecurityConstants.HEADER_STRING);
        if (StringUtils.hasText(bearToken) && bearToken.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            return bearToken.split(" ")[1];
        }
        return null;
    }

}
