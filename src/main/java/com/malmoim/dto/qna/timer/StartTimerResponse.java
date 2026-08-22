package com.malmoim.dto.qna.timer;

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
public class StartTimerResponse {
    private Long roomNo;
    private QnaPhase status;
    private LocalDateTime questionStartedAt;
    private LocalDateTime questionEndedAt;
}
