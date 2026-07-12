package com.malmoim.mapper;


import com.malmoim.domain.Member;

public interface MemberMapper {

    Member getMemberByEmail(String email);
    // member 테이블에 회원 추가.
    void insertMember(Member member);

}
