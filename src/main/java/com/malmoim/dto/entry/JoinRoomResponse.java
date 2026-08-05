package com.malmoim.dto.entry;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JoinRoomResponse {
    private Long participantNo;
    private String message;
    private String participantToken;
}
