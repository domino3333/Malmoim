package com.malmoim.dto.room;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyRoomResponse {
    private Long roomNo;
    private Long hostNo;
    private String title;
    private String code;
    private Integer capacity;
    private LocalDateTime createdAt;
    private String type;
    private String visibility;
}
