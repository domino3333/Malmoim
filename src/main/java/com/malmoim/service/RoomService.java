package com.malmoim.service;

import com.malmoim.domain.Room;
import com.malmoim.dto.room.CreateQnaRoomDto;

import java.util.List;

public interface RoomService {

    void createQnARoom(CreateQnaRoomDto dto,String hostEmail);

    List<Room> getMyRooms(String hostEmail);
}
