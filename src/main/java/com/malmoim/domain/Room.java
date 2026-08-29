package com.malmoim.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Room {
    private Long no; // room 테이블 PK
    private Long hostNo;
    private String title;
    private String code;
    private Integer capacity;
    private String password;
    private LocalDateTime createdAt;
    private String type;
    private String visibility;

}
