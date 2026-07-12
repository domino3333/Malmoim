package com.malmoim.service;

import com.malmoim.domain.Room;
import com.malmoim.dto.room.qna.CreateQnaRoomRequest;
import com.malmoim.dto.room.MyRoomsResponseDto;

public interface RoomService {

    void createQnaRoom(CreateQnaRoomRequest dto,String hostEmail);

    MyRoomsResponseDto getMyRooms(String hostEmail, int page, int size);

    // 로그인한 호스트 소유의 방 조회.
    Room getOwnedRoomByNo(long roomNo, String hostEmail);

    // 방 번호 기준 단일 방 조회.
    Room getRoomByNo(Long roomNo);
}
