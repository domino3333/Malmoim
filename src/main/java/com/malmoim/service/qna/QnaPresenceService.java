package com.malmoim.service.qna;

import com.malmoim.dto.qna.presence.ActiveParticipantResponse;
import com.malmoim.dto.qna.presence.ParticipantPresenceResponse;
import com.malmoim.service.room.RoomService;
import com.malmoim.websocket.qna.QnaPresenceRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QnaPresenceService {

    private final QnaPresenceRegistry qnaPresenceRegistry;
    private final RoomService roomService;

    // 호스트 소유권 확인 후 현재 접속자 목록과 인원수 조회
    public ParticipantPresenceResponse getHostParticipantSnapshot(long roomNo, String hostEmail) {
        roomService.validateRoomOwnership(roomNo, hostEmail);
        return getActiveParticipantSnapshot(roomNo);
    }


    public ParticipantPresenceResponse getActiveParticipantSnapshot(Long roomNo){

        List<QnaPresenceRegistry.PresenceSession> presenceSession = qnaPresenceRegistry.getActiveParticipants(roomNo);

        List<ActiveParticipantResponse> activeParticipantList = new ArrayList<>();

        for (QnaPresenceRegistry.PresenceSession session : presenceSession) {
            Long participantNo = session.getParticipantNo();
            String nickname = session.getNickname();
            activeParticipantList.add(new ActiveParticipantResponse(participantNo,nickname));
        }

        return new ParticipantPresenceResponse(activeParticipantList.size(),activeParticipantList);
    }
}
