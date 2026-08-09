package com.malmoim.dto.qna;

import com.malmoim.domain.Participant;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ParticipantListCountResponse {
    private Integer count;
    private List<Participant> list;
}
