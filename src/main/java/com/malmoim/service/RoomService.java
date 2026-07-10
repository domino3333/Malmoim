package com.malmoim.service;

import com.malmoim.domain.Room;
import com.malmoim.dto.room.qna.CreateQnaRoomRequest;
import com.malmoim.dto.room.MyRoomsResponseDto;

public interface RoomService {

    void createQnaRoom(CreateQnaRoomRequest dto,String hostEmail);

    MyRoomsResponseDto getMyRooms(String hostEmail, int page, int size);

    Room getMyOneRoom(long no, String hostEmail);

    Room getOneRoomWithOnlyNo(Long no);
}
