package com.malmoim.service;

import com.malmoim.dto.room.CreateQnaRoomDto;

public interface RoomService {

    void createQnARoom(CreateQnaRoomDto dto,String hostEmail);
}
