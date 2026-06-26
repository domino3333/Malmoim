package com.malmoim.dto.room.qna.timer;

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
    private String status;
    private LocalDateTime questionStartedAt;
    private LocalDateTime questionEndedAt;
}
