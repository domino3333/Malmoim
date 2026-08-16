package com.malmoim.websocket.qna;

import com.malmoim.dto.qna.ActiveParticipantResponse;
import com.malmoim.dto.qna.ParticipantListCountResponse;
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
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class QnaPresenceEventListener {

    private final QnaPresenceRegistry qnaPresenceRegistry;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @EventListener
    public void handleConnected(SessionConnectedEvent event) {
        Principal user = event.getUser();

        if (!(user instanceof Authentication authentication)) {
            return;
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof ParticipantPrincipal)) {
            return;
        }

        ParticipantPrincipal participantPrincipal = (ParticipantPrincipal) principal;
        log.info("연결된 참여자의 닉네임:{}", participantPrincipal.getNickname());

        // event에서 Message 객체를 가져오기 위해 좀 더 편한 wrap사용
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String sessionId = accessor.getSessionId();

        if (sessionId == null) {
            return;
        }

        log.info("참여자 연결 성공 웹소켓 세션아이디:{}", sessionId);

        qnaPresenceRegistry.connect(
                sessionId,
                participantPrincipal.getRoomNo(),
                participantPrincipal.getParticipantNo(),
                participantPrincipal.getNickname()
        );

        Long roomNo = participantPrincipal.getRoomNo();


        List<QnaPresenceRegistry.PresenceSession> presenceSession = qnaPresenceRegistry.getActiveParticipants(roomNo);


        int participantCount = presenceSession.size();

        // 리스트에서 no와 닉네임을 꺼내고 그걸로 ActiveParticipantResponse 리스트를 만들어야함
        List<ActiveParticipantResponse> activeParticipantList = new ArrayList<>();

        for (QnaPresenceRegistry.PresenceSession session : presenceSession) {
            Long participantNo = session.getParticipantNo();
            String nickname = session.getNickname();
            activeParticipantList.add(new ActiveParticipantResponse(participantNo,nickname));
        }

        ParticipantListCountResponse response = new ParticipantListCountResponse(participantCount, activeParticipantList);
        log.info("connect리스너에서 현재 참여자 count:{}",response.getParticipantCount());
        simpMessagingTemplate.convertAndSend("/topic/qna/" + roomNo + "/participants", response);

    }

    @EventListener
    public void handleDisconnected(SessionDisconnectEvent event) {

        String sessionId = event.getSessionId();
        log.info("삭제된 sessionId:{}", sessionId);
        QnaPresenceRegistry.PresenceSession disconnectedSession = qnaPresenceRegistry.disconnect(sessionId);

        if (disconnectedSession == null) {
            log.info("disconnectedSession이 비어있습니다.");
            return;
        }

        Long roomNo = disconnectedSession.getRoomNo();

        List<QnaPresenceRegistry.PresenceSession> activeParticipants =
                qnaPresenceRegistry.getActiveParticipants(roomNo);
        log.info("참여자 퇴장 후 현재 참여자 수:{}", activeParticipants.size());

        // 함수에서 사이즈로 참여자 수 받기
        int participantCount = activeParticipants.size();

        List<ActiveParticipantResponse> activeParticipantList = new ArrayList<>();


        for(QnaPresenceRegistry.PresenceSession session : activeParticipants){
            Long participantNo = session.getParticipantNo();
            String nickname = session.getNickname();
            activeParticipantList.add(new ActiveParticipantResponse(participantNo,nickname));
        }


        ParticipantListCountResponse response = new ParticipantListCountResponse(participantCount, activeParticipantList);
        log.info("disconnect리스너에서 현재 참여자 count:{}",response.getParticipantCount());


        // 이를 구독하는 destination에 변경된 참여자 수를 방송
        simpMessagingTemplate.convertAndSend("/topic/qna/" + roomNo + "/participants", response);
    }


}
