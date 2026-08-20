package com.malmoim.dto.qna;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UpdateRoomStatusResponse {
    private Long roomNo;
    private String status;
    private LocalDateTime questionStartedAt;
    private LocalDateTime questionEndedAt;
}
