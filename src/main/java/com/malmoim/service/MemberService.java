package com.malmoim.service;


import com.malmoim.dto.auth.SignUpRequest;

public interface MemberService {

    void signUp(SignUpRequest dto);
}
