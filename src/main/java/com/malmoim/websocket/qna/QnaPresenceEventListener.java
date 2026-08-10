package com.malmoim.websocket.qna;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

@Component
@RequiredArgsConstructor
public class QnaPresenceEventListener {

    private final QnaPresenceRegistry qnaPresenceRegistry;

    @EventListener
    public void handleConnected(SessionConnectedEvent event){

    }
}
