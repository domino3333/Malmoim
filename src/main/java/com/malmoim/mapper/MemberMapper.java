package com.malmoim.mapper;


import com.malmoim.domain.Member;

public interface MemberMapper {

    Member getMemberByEmail(String email);

}
