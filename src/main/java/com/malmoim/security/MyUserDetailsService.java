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

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {


    private final MemberMapper memberMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Member member = memberMapper.getMemberByEmail(email);
        if (member == null){
            throw new RuntimeException("loadUserByUsername: member 없음");
        }

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(member.getRole());

        return MyUser.builder()
                .email(member.getEmail())
                .password(member.getPassword())
                .name(member.getName())
                .authority(authority)
                .createdAt(member.getCreatedAt())
                .build();
    }
}
