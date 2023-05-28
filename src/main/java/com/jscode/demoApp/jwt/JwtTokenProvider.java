package com.jscode.demoApp.jwt;

import com.jscode.demoApp.dto.UserPrincipal;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
public class JwtTokenProvider {
    @Getter
    private final String secret;
    @Getter private final long tokenTime;
    private Key key;

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String HEADER_PREFIX = "Bearer";

    public JwtTokenProvider(@Value("${jwt.secret}") String secret, @Value("${jwt.tokenTime}") long tokenTime) {
        this.secret = secret;
        this.tokenTime = tokenTime * 60 *  1000;
    }

    @PostConstruct
    public void init(){
        log.info("init");
        byte[] keyBytes = Decoders.BASE64.decode(this.secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(Authentication authentication){
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        long current = (new Date()).getTime();

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenTime);

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .claim("auth", userPrincipal.getId())
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    public Authentication getAuthentication(String token){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        UserPrincipal userPrincipal = UserPrincipal.of(claims.get("auth", Long.class), claims.getSubject(), "");

        return new UsernamePasswordAuthenticationToken(userPrincipal, token, null);
    }

    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

}
