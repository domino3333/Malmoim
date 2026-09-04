package com.malmoim.dto.room;

import lombok.Data;

import java.util.List;

@Data
public class MyRoomsResponse {

    private List<MyRoomResponse> rooms;
    private int totalCount;
}
