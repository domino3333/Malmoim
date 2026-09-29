package com.malmoim.websocket.qna;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class QnaPresenceRegistry {

    private final Map<String, PresenceSession> sessions = new ConcurrentHashMap<>();

    // 연결 승인 시 정원 확인과 세션 등록을 함께 처리한다.
    public synchronized void admit(
            String sessionId,
            Long roomNo,
            Long participantNo,
            String nickname,
            int capacity
    ) {
        if (sessionId == null) {
            throw new MessagingException("SESSION_ID_MISSING");
        }
        if (sessions.containsKey(sessionId)) {
            return;
        }

        Set<Long> activeParticipantNos = new HashSet<>();
        for (PresenceSession session : sessions.values()) {
            if (Objects.equals(session.getRoomNo(), roomNo)) {
                activeParticipantNos.add(session.getParticipantNo());
            }
        }

        if (!activeParticipantNos.contains(participantNo)
                && activeParticipantNos.size() >= capacity) {
            throw new MessagingException("ROOM_FULL");
        }

        sessions.put(sessionId, new PresenceSession(
                sessionId, roomNo, participantNo, nickname
        ));
    }

    public synchronized PresenceSession disconnect(String sessionId) {
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
