package com.malmoim.dto.room;

import com.malmoim.domain.Room;
import lombok.Data;

import java.util.List;

@Data
public class MyRoomsResponseDto {

    private List<Room> rooms;
    private int totalCount;
}
