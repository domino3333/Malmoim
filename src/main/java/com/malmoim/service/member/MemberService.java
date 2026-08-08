package com.malmoim.service.member;


import com.malmoim.dto.auth.SignUpRequest;

public interface MemberService {

    void signUp(SignUpRequest dto);
}
