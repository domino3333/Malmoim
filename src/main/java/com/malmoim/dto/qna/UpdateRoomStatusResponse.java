package com.malmoim.dto.qna;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateRoomStatusResponse {
    private Long roomNo;
    private String status;
}
