package com.malmoim.websocket.qna;

import com.malmoim.security.ParticipantPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class QnaPresenceEventListener {

    private final QnaPresenceRegistry qnaPresenceRegistry;

    @EventListener
    public void handleConnected(SessionConnectedEvent event){
        Principal user = event.getUser();

        if(!(user instanceof Authentication authentication)){
            return;
        }

        if(!(authentication.getPrincipal() instanceof ParticipantPrincipal participantPrincipal)){
            return;
        }

        System.out.println("연결된 참여자:" + participantPrincipal.getNickname());

    }
}
