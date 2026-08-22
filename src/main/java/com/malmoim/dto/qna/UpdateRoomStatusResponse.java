package com.malmoim.dto.qna;


import com.malmoim.domain.QnaPhase;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UpdateRoomStatusResponse {
    private Long roomNo;
    private QnaPhase status;
    private LocalDateTime questionStartedAt;
    private LocalDateTime questionEndedAt;
}
