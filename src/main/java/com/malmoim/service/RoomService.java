package com.malmoim.service;

import com.malmoim.domain.Room;
import com.malmoim.dto.room.qna.CreateQnaRoomDto;
import com.malmoim.dto.room.MyRoomsResponseDto;

public interface RoomService {

    void createQnARoom(CreateQnaRoomDto dto,String hostEmail);

    MyRoomsResponseDto getMyRooms(String hostEmail, int page, int size);

    Room getMyOneRoom(long no, String hostEmail);
}
