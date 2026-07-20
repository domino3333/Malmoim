package com.malmoim.dto.entry;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InsertParticipantResponse {
    private Long no;
    private String message;
    private String token;
}
