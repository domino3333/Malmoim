package com.malmoim.dto.qna;

import com.malmoim.domain.Participant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ParticipantListCountResponse {
    private Integer count;
    private List<Participant> list;
}
