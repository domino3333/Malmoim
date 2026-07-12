package com.malmoim.controller;


import com.malmoim.dto.auth.LoginRequest;
import com.malmoim.dto.auth.LoginResponse;
import com.malmoim.dto.auth.SignUpRequest;
import com.malmoim.security.jwt.JwtTokenProvider;
import com.malmoim.service.MemberService;
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
public class AuthController {


    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;




    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest dto){

        log.info("login 진입");
        log.info("email:{}",dto.getEmail());

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(dto.getEmail(),dto.getPassword());
        Authentication authentication = authenticationManager.authenticate(token);
        String accessToken = jwtTokenProvider.createAccessToken(authentication);

        return ResponseEntity.ok(new LoginResponse(accessToken));
    }


    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequest dto){

        log.info("signUp 진입");
        log.info("email:{}",dto.getEmail());

        memberService.signUp(dto);

        return ResponseEntity.ok("가입완료");
    }



}
