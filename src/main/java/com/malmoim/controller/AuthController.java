package com.malmoim.controller;


import com.malmoim.dto.LoginDto;
import com.malmoim.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {


    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto dto){

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(dto.getEmail(),dto.getPassword());

        Authentication authentication = authenticationManager.authenticate(token);

        String jsonWebToken = jwtTokenProvider.createToken(authentication);


        return ResponseEntity.ok(jsonWebToken);
    }
}
