package com.malmoim.security;


import com.malmoim.domain.Member;
import com.malmoim.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
// 회원 정보를 Spring Security의 UserDetails로 변환하는 서비스.
public class MemberUserDetailsService implements UserDetailsService {


    private final MemberMapper memberMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Member member = memberMapper.getMemberByEmail(email);
        if (member == null){
            throw new UsernameNotFoundException("loadUserByUsername: member 없음");
        }

        //권한이 하나뿐이라 스트림을 돌릴 필요 x
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_"+member.getRole());
        List<SimpleGrantedAuthority> list = new ArrayList<>();
        list.add(authority);

        return MemberPrincipal.builder()
                .email(member.getEmail())
                .password(member.getPassword())
                .name(member.getName())
                .authorities(list)
                .createdAt(member.getCreatedAt())
                .build();
    }
}
