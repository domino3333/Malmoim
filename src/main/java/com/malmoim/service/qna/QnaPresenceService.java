package com.malmoim.service.qna;

import com.malmoim.dto.qna.presence.ActiveParticipantResponse;
import com.malmoim.dto.qna.presence.ParticipantListCountResponse;
import com.malmoim.websocket.qna.QnaPresenceRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QnaPresenceService {

    private final QnaPresenceRegistry qnaPresenceRegistry;


    public ParticipantListCountResponse getActiveParticipantSnapshot(Long roomNo){

        List<QnaPresenceRegistry.PresenceSession> presenceSession = qnaPresenceRegistry.getActiveParticipants(roomNo);

        List<ActiveParticipantResponse> activeParticipantList = new ArrayList<>();

        for (QnaPresenceRegistry.PresenceSession session : presenceSession) {
            Long participantNo = session.getParticipantNo();
            String nickname = session.getNickname();
            activeParticipantList.add(new ActiveParticipantResponse(participantNo,nickname));
        }

        return new ParticipantListCountResponse(activeParticipantList.size(),activeParticipantList);
    }
}
