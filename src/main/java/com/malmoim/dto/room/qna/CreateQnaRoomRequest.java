package com.malmoim.dto.room.qna;


import lombok.Data;

@Data
public class CreateQnaRoomRequest {

    private String title;
    private Integer capacity;
    private String password;
    private Boolean isPrivate;

}
