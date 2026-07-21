package com.malmoim.security.jwt;


import com.malmoim.security.MemberPrincipal;
import com.malmoim.security.MemberUserDetailsService;
import com.malmoim.security.ParticipantPrincipal;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


/**
 * 토큰을 들고 올 때 토큰을 검증하는 필터
 * */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final JwtTokenProvider jwtTokenProvider;
    private final MemberUserDetailsService memberUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        //로그인 때는 토큰이 당연히 null 값이므로 여길 스킵하고 바로 다음 필터로 넘어가짐
        if(header!=null && header.startsWith("Bearer ")){
            String token = header.substring(7);

            if(jwtTokenProvider.validateToken(token)){

                String type = jwtTokenProvider.extractType(token);

                if(type.equals("PARTICIPANT")){
                    //참여자 토큰일 시
                    String nickname = jwtTokenProvider.extractNickname(token);
                    Long participantNo = jwtTokenProvider.extractParticipantNo(token);
                    Long roomNo = jwtTokenProvider.extractRoomNo(token);

                    ParticipantPrincipal participantPrincipal = new ParticipantPrincipal(roomNo,participantNo,nickname);

                    UsernamePasswordAuthenticationToken authenticationToken
                            = new UsernamePasswordAuthenticationToken(participantPrincipal,null,participantPrincipal.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                }else{
                    // 호스트 토큰일 시
                    String email = jwtTokenProvider.extractEmail(token);
                    MemberPrincipal memberPrincipal = (MemberPrincipal) memberUserDetailsService.loadUserByUsername(email);

                    UsernamePasswordAuthenticationToken authenticationToken
                            = new UsernamePasswordAuthenticationToken(memberPrincipal,null,memberPrincipal.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }




            }

        }
        filterChain.doFilter(request,response);


    }
}
