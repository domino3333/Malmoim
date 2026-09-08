package com.malmoim.security.websocket;

import com.malmoim.security.MemberPrincipal;
import com.malmoim.security.MemberUserDetailsService;
import com.malmoim.security.ParticipantPrincipal;
import com.malmoim.security.jwt.JwtTokenProvider;
import com.malmoim.service.room.RoomService;
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

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class StompJwtChannelInterceptor implements ChannelInterceptor {


    private final JwtTokenProvider jwtTokenProvider;
    private final MemberUserDetailsService memberUserDetailsService;
    private final RoomService roomService;


    @Override
    public @Nullable Message<?> preSend(Message<?> message, MessageChannel channel) {

        //일반적인 message 객체는 stomp의 정보를 다루기 불편해서 더 편한 StompHeaderAccessor를 사용
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        /*
            stomp의 명령어 종류

            클라이언트 → 서버
            CONNECT, SUBSCRIBE, SEND, UNSUBSCRIBE, DISCONNECT

            서버 → 클라이언트
            CONNECTED, MESSAGE, RECEIPT, ERROR

         */

        if (accessor == null || accessor.getCommand() == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            authorizeConnect(accessor);
        }

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            authorizeSubscribe(accessor);
        }

        if (StompCommand.SEND.equals(accessor.getCommand())) {
            authorizeSend(accessor);
        }


        //검사가 끝난 connect 메시지를 다음 처리단계로 통과시킴
        return message;

    }

    private void authorizeConnect(StompHeaderAccessor accessor) {

        String header = accessor.getFirstNativeHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            throw new MessagingException("STOMP 토큰이 없습니다");
        }

        String token = header.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            throw new MessagingException("유효하지 않은 STOMP 토큰입니다");
        }

        Authentication authentication;
        String type = jwtTokenProvider.extractType(token);

        if ("PARTICIPANT".equals(type)) {
            ParticipantPrincipal participant =
                    new ParticipantPrincipal(
                            jwtTokenProvider.extractRoomNo(token),
                            jwtTokenProvider.extractParticipantNo(token),
                            jwtTokenProvider.extractNickname(token)
                    );

            authentication
                    = new UsernamePasswordAuthenticationToken(participant, null, participant.getAuthorities());

        } else if (type == null) {
            //호스트일 경우
            UserDetails member =
                    memberUserDetailsService.loadUserByUsername(jwtTokenProvider.extractEmail(token));
            authentication = new UsernamePasswordAuthenticationToken(member, null, member.getAuthorities());
        } else {
            throw new MessagingException("알 수 없는 사용자의 토큰입니다.");

        }
        // 현재 STOMP/WebSocket 세션의 사용자 정보로 등록
        // 이후 같은 연결에서 SEND 프레임이 오면 컨트롤러에서 인증정보로부터 사용자를 꺼낼 수 있음
        accessor.setUser(authentication);
    }

    private Authentication requireAuthentication(StompHeaderAccessor accessor) {
        if (!(accessor.getUser()
                instanceof Authentication authentication)
                || !authentication.isAuthenticated()) {

            throw new MessagingException(
                    "인증되지 않은 WebSocket 요청입니다"
            );
        }

        return authentication;
    }


    private long extractQnaRoomNo(String destination) {
        String prefix = "/topic/qna/";

        if (destination == null || !destination.startsWith(prefix)) {
            throw new MessagingException("허용되지 않은 웹소켓 구독 주소입니다.");
        }

        String remainingPath = destination.substring(prefix.length());

        String[] parts = remainingPath.split("/", -1);

        if (parts.length == 0 || parts[0].isBlank() || parts.length > 2) {
            throw new MessagingException("잘못된 웹소켓 구독 주소입니다.");
        }

        if (parts.length == 2) {
            String suffix = parts[1];

            boolean allowedSuffix = "phase".equals(suffix)
                    || "participants".equals(suffix) || "result".equals(suffix);

            if (!allowedSuffix) {
                throw new MessagingException("허용되지 않은 QnA 채널입니다.");
            }

        }

        try {
            return Long.parseLong(parts[0]);
        } catch (NumberFormatException e) {
            throw new MessagingException("구독 주소의 방 번호가 잘못되었습니다.", e);
        }

    }


    private void authorizeSubscribe(StompHeaderAccessor accessor) {
        Authentication authentication = requireAuthentication(accessor);

        long requestedRoomNo = extractQnaRoomNo(accessor.getDestination());
        Object principal = authentication.getPrincipal();


        if (principal instanceof ParticipantPrincipal participant) {

            if (!Objects.equals(participant.getRoomNo(), requestedRoomNo)) {
                throw new MessagingException("다른 방의 채널은 구독할 수 없습니다.");
            }
            return;
        }

        if (principal instanceof MemberPrincipal member) {
            roomService.validateRoomOwnership(requestedRoomNo, member.getUsername());
            return;
        }

        throw new MessagingException("구독 권한을 확인할 수 없는 사용자입니다.");

    }


    private void authorizeSend(StompHeaderAccessor accessor) {
        Authentication authentication = requireAuthentication(accessor);

        String destination = accessor.getDestination();

        if (destination == null) {
            throw new MessagingException("메세지의 목적지가 없습니다.");
        }

        //클라이언트가 브로커 채널에 직접 메시지를 보내는 행위 차단
        if (destination.startsWith("/topic")) {
            throw new MessagingException("/topic 채널로 직접 발행할 수 없습니다.");
        }

        if ("/app/qna/register".equals(destination)) {
            if (!(authentication.getPrincipal() instanceof ParticipantPrincipal)) {
                throw new MessagingException("참여자만 질문을 등록할 수 있습니다.");
            }
            return;
        }

        throw new MessagingException("허용되지 않은 메시지 목적지입니다.");

    }


}
