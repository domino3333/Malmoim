package com.malmoim.websocket.qna;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class QnaPresenceRegistry {

    private final Map<String,PresenceSession> sessions = new ConcurrentHashMap<>();

    public void connect(
            String sessionId,
            Long roomNo,
            Long participantNo,
            String nickname
    ){
        PresenceSession session = new PresenceSession(
                sessionId,
                roomNo,
                participantNo,
                nickname
        );

        sessions.put(sessionId,session);
    }


    @Getter
    @AllArgsConstructor
    public static class PresenceSession{
        private String sessionId;
        private Long roomNo;
        private Long participantNo;
        private String nickname;
    }
}
