package com.malmoim.service.impl;

import com.malmoim.domain.Member;
import com.malmoim.dto.auth.SignUpRequest;
import com.malmoim.mapper.MemberMapper;
import com.malmoim.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void signUp(SignUpRequest dto) {

        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        memberMapper.insertMember(Member.builder()
                .email(dto.getEmail())
                .name(dto.getName())
                .password(encodedPassword)
                .build()
        );
    }
}
