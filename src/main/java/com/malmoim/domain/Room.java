package com.malmoim.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Room {
    private Long no;
    private Long hostNo;
    private String title;
    private String code;
    private Integer capacity;
    private String password;
    private LocalDateTime createdAt;
    private String status;
    private String type;
    private String visibility;
    private LocalDateTime questionStartedAt;
    private LocalDateTime questionEndedAt;

}
