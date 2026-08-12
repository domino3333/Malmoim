package com.malmoim.websocket.qna;

import com.malmoim.security.ParticipantPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class QnaPresenceEventListener {

    private final QnaPresenceRegistry qnaPresenceRegistry;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @EventListener
    public void handleConnected(SessionConnectedEvent event){
        Principal user = event.getUser();

        if(!(user instanceof Authentication authentication)){
            return;
        }

        Object principal = authentication.getPrincipal();

        if(!(principal instanceof ParticipantPrincipal)){
            return;
        }

        ParticipantPrincipal participantPrincipal =(ParticipantPrincipal) principal;
        log.info("연결된 참여자의 닉네임:{}",participantPrincipal.getNickname());

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String sessionId = accessor.getSessionId();

        if(sessionId == null){
            return;
        }

        log.info("참여자 연결 성공, WebSocket sessionId:{}",sessionId);

        qnaPresenceRegistry.connect(
                sessionId,
                participantPrincipal.getRoomNo(),
                participantPrincipal.getParticipantNo(),
                participantPrincipal.getNickname()
        );

        log.info("현재 참여자 수:{}",qnaPresenceRegistry.getActiveParticipants(participantPrincipal.getRoomNo()).size());

    }

    @EventListener
    public void handleDisconnected(SessionDisconnectEvent event){

        String sessionId = event.getSessionId();
        log.info("삭제된 sessionId:{}",sessionId);
        QnaPresenceRegistry.PresenceSession disconnectedSession = qnaPresenceRegistry.disconnect(sessionId);

        if(disconnectedSession == null){
            log.info("disconnectedSession이 비어있습니다.");
            return;
        }

        Long roomNo = disconnectedSession.getRoomNo();

        List<QnaPresenceRegistry.PresenceSession> activeParticipants =
                qnaPresenceRegistry.getActiveParticipants(roomNo);
        log.info("참여자 퇴장 후 현재 참여자 수:{}",activeParticipants.size());
    }


    


}
