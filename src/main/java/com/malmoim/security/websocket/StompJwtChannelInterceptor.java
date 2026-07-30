package com.malmoim.security.websocket;

import com.malmoim.security.MemberPrincipal;
import com.malmoim.security.MemberUserDetailsService;
import com.malmoim.security.ParticipantPrincipal;
import com.malmoim.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompJwtChannelInterceptor implements ChannelInterceptor {


    private final JwtTokenProvider jwtTokenProvider;
    private final MemberUserDetailsService memberUserDetailsService;


    @Override
    public @Nullable Message<?> preSend(Message<?> message, MessageChannel channel) {

        //일반적인 message 객체는 stomp의 정보를 다루기 불편해서 더 편한 StompHeaderAccessor를 사용
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        /*stomp의 명령어 종류
//         클라이언트 → 서버
//         CONNECT, SUBSCRIBE, SEND, UNSUBSCRIBE, DISCONNECT

//         서버 → 클라이언트
//         CONNECTED, MESSAGE, RECEIPT, ERROR
         */
        if (accessor == null || !StompCommand.CONNECT.equals(accessor.getCommand())) {
            return message;
        }

        String header = accessor.getFirstNativeHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            throw new MessagingException("STOMP 토큰이 없습니다");
        }

        String token = header.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            throw new MessagingException("유효하지 않은 STOMP 토큰입니다");
        }

        Authentication authentication;

        if ("PARTICIPANT".equals(jwtTokenProvider.extractType(token))) {
            ParticipantPrincipal participant =
                    new ParticipantPrincipal(
                            jwtTokenProvider.extractRoomNo(token),
                            jwtTokenProvider.extractParticipantNo(token),
                            jwtTokenProvider.extractNickname(token)
                    );

            authentication
                    = new UsernamePasswordAuthenticationToken(participant, null, participant.getAuthorities());

        } else {
            UserDetails member =
                    memberUserDetailsService.loadUserByUsername(jwtTokenProvider.extractEmail(token));
            authentication = new UsernamePasswordAuthenticationToken(member, null, member.getAuthorities());
        }

        accessor.setUser(authentication);


        return message;

    }
}
