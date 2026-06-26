package com.malmoim.dto.room.qna.timer;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StartTimerResponse {
    private Long roomNo;
    private String status;
    private LocalDateTime questionStartedAt;
    private LocalDateTime questionEndedAt;
}
