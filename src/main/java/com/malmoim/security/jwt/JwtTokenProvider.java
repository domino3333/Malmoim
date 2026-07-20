package com.malmoim.security.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;


@Component
public class JwtTokenProvider {


    private final SecretKey key;
    private final Long expiration;


    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration}") Long expiration) {

        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    public boolean validateToken(String token){
        try{
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            return true;
        }catch(JwtException | IllegalArgumentException e){
            return false;
        }
    }

    // JWT subject에 저장된 회원 이메일 추출.
    public String extractEmail(String token){
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }


    // 인증된 회원 정보 기반 액세스 토큰 생성.
    public String createAccessToken(Authentication authentication){
        String email = authentication.getName();

        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(role->role.startsWith("ROLE_"))
                .collect(Collectors.joining(","));


        Date now = new Date();
        Date expiry = new Date(now.getTime()+expiration);

        return Jwts.builder()
                .subject(email)
                .claim("roles",roles)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    // 참여자용 토큰
    public String createParticipantToken(long participantNo,long roomNo,long nickname) {

        return Jwts.builder()
                .claim("participantNo",participantNo)
                .claim("roomNo",roomNo)
                .claim("nickname",nickname)
                .signWith(key)
                .compact();

    }



}
