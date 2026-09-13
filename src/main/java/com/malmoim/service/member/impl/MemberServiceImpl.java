package com.malmoim.service.member.impl;

import com.malmoim.domain.Member;
import com.malmoim.dto.auth.SignUpRequest;
import com.malmoim.mapper.MemberMapper;
import com.malmoim.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void signUp(SignUpRequest dto) {

        String password = dto.getPassword();
        if (password == null || password.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호를 입력해주세요");
        }
        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "비밀번호는 UTF-8 기준 72바이트 이하여야 합니다");
        }

        String encodedPassword = passwordEncoder.encode(password);

        memberMapper.insertMember(Member.builder()
                .email(dto.getEmail())
                .name(dto.getName())
                .password(encodedPassword)
                .build()
        );
    }
}
