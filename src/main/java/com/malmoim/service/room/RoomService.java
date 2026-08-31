package com.malmoim.service.room;

import com.malmoim.dto.room.MyRoomsResponse;

public interface RoomService {

    MyRoomsResponse getMyRooms(String hostEmail, int page, int size);
}
