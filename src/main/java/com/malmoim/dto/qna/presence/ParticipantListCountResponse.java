package com.malmoim.dto.qna.presence;

import com.malmoim.domain.Participant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ParticipantListCountResponse {
    private Integer participantCount;
    private List<ActiveParticipantResponse> participants; // 현재 접속 중인 참여자 리스트
}
