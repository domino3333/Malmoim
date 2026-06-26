package com.malmoim.dto.room.qna.timer;

import lombok.Data;

@Data
public class StartTimerResponse {
    private Long roomNo;
    private String status;
    private Long questionStartedAt;
    private Long questionEndedAt;
}
