package com.malmoim.service.room;

import com.malmoim.domain.Room;
import com.malmoim.dto.qna.QnaRoomInfoResponse;
import com.malmoim.dto.room.MyRoomsResponse;

public interface RoomService {

    MyRoomsResponse getMyRooms(String hostEmail, int page, int size);

    // 로그인한 호스트 소유의 방 조회
    QnaRoomInfoResponse getOwnedRoomByNo(long roomNo, String hostEmail);

    // 방 번호 기준 단일 방 조회
    QnaRoomInfoResponse getRoomByNo(Long roomNo);
}
