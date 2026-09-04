package com.malmoim.dto.entry;


import lombok.Data;

@Data
public class JoinRoomRequest {
    private Long roomNo;
    private String nickname;
    private String password;
}
