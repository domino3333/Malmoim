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
import java.util.Date;
import java.util.stream.Collectors;


@Component
public class JwtTokenProvider {


    private final SecretKey key;
    private final Long expiration;


    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey
            , @Value("${jwt.expiration}") Long expiration) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
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

    public String getEmail(String token){
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }


    public String createToken(Authentication authentication){
        String email = authentication.getName();

        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(role->role.startsWith("ROLE_"))
                .collect(Collectors.joining(","));


        Date now = new Date();
        Date expiry = new Date(now.getTime()+expiration);

        return Jwts.builder()
                .subject(email)
                .claim("roles",role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }



}
