package com.malmoim.dto.room;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MyRoomResponse {
    private Long no; // room 테이블 PK
    private Long hostNo;
    private String title;
    private String code;
    private Integer capacity;
    private LocalDateTime createdAt;
    private String type;
    private String visibility;
}
