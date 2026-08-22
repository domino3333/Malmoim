package com.malmoim.dto.qna;

import com.malmoim.domain.QnaPhase;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QnaRoomInfoResponse {
    private Long no;
    private Long hostNo;
    private String password;
    private String title;
    private String code;
    private Integer capacity;
    private LocalDateTime createdAt;
    private String type;
    private String visibility;
    private QnaPhase status;
}
