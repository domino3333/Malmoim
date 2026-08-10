package com.malmoim.websocket.qna;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class QnaPresenceRegistry {

    private final Map<String, PresenceSession> sessions = new ConcurrentHashMap<>();

    public void connect(
            String sessionId,
            Long roomNo,
            Long participantNo,
            String nickname
    ) {
        PresenceSession session = new PresenceSession(
                sessionId,
                roomNo,
                participantNo,
                nickname
        );

        sessions.put(sessionId, session);
    }

    public PresenceSession disconnect(String sessionId) {
        return sessions.remove(sessionId);
    }

    public List<PresenceSession> getActiveParticipants(Long roomNo) {
        return sessions.values().stream()
                .filter(session -> Objects.equals(session.getRoomNo(), roomNo))
                .collect(Collectors.toMap(
                        PresenceSession::getParticipantNo,
                        session -> session,
                        (existing, duplicate) -> existing,
                        LinkedHashMap::new
                )).values()
                .stream()
                .toList();
    }

    @Getter
    @AllArgsConstructor
    public static class PresenceSession {
        private String sessionId;
        private Long roomNo;
        private Long participantNo;
        private String nickname;
    }
}
