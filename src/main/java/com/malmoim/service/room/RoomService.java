package com.malmoim.service.room;

import com.malmoim.dto.room.MyRoomResponse;
import com.malmoim.dto.room.MyRoomsResponse;

import java.util.List;

public interface RoomService {

    MyRoomsResponse getMyRooms(String hostEmail, int page, int size);

    // 방 소유권 검증 및 권한이 없는 요청 차단
    void validateRoomOwnership(long roomNo, String hostEmail);

    List<MyRoomResponse> getMyRecentRooms(String hostEmail);

    MyRoomsResponse getSearchedRooms(String hostEmail, String keyword,int page, int size);
}
