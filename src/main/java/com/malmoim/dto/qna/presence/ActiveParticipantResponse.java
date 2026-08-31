package com.malmoim.dto.qna.presence;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ActiveParticipantResponse {
    private Long participantNo;
    private String nickname;
}
