package com.malmoim.service;

import com.malmoim.domain.Room;
import com.malmoim.dto.room.CreateQnaRoomDto;
import com.malmoim.dto.room.MyRoomsResponseDto;

import java.util.List;

public interface RoomService {

    void createQnARoom(CreateQnaRoomDto dto,String hostEmail);

    MyRoomsResponseDto getMyRooms(String hostEmail, int page, int size);
}
