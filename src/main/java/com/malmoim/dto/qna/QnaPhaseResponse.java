package com.malmoim.dto.qna;

import com.malmoim.domain.QnaPhase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QnaPhaseResponse {
    private Long roomNo;
    private QnaPhase status;
    private LocalDateTime phaseStartedAt;
    private LocalDateTime phaseEndedAt;
}
