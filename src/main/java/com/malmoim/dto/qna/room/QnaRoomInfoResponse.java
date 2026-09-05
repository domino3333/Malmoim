package com.malmoim.dto.qna.room;

import com.malmoim.domain.QnaPhase;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QnaRoomInfoResponse {
    private Long roomNo;
    private Long hostNo;
    private String title;
    private String code;
    private Integer capacity;
    private LocalDateTime createdAt;
    private String type;
    private String visibility;
    private QnaPhase status;
    private LocalDateTime phaseStartedAt;
    private LocalDateTime phaseEndedAt;
}
