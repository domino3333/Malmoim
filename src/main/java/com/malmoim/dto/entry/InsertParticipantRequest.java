package com.malmoim.dto.entry;


import lombok.Data;

@Data
public class InsertParticipantRequest {
    private Long roomNo;
    private String nickname;
}
