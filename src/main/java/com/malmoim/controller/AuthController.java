package com.malmoim.controller;


import com.malmoim.dto.auth.LoginDto;
import com.malmoim.dto.auth.LoginResponse;
import com.malmoim.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {


    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto dto){

        log.info("login 진입");
        log.info("email:{}",dto.getEmail());
        log.info("password:{}",dto.getPassword());

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(dto.getEmail(),dto.getPassword());

        Authentication authentication = authenticationManager.authenticate(token);

        String accessToken = jwtTokenProvider.createToken(authentication);


        return ResponseEntity.ok(new LoginResponse(accessToken));
    }
}
