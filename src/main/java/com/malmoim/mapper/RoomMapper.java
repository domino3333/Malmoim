package com.malmoim.mapper;


import com.malmoim.domain.Room;

import java.util.List;

public interface RoomMapper {


    void CreateQnARoom(Room room);

    List<Room> getMyRooms(Long no);
}
