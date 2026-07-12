package com.malmoim.mapper;


import com.malmoim.domain.Room;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoomMapper {


    // room 테이블에 방 추가.
    void insertRoom(Room room);

    List<Room> getMyRooms(@Param("no") Long no,@Param("offset") int offset,@Param("size") int size);

    Integer countMyRooms(Long no);

    // 방 번호와 호스트 번호가 일치하는 방 조회.
    Room selectRoomByNoAndHostNo(Long no, Long hostNo);

    void updateRoomStatus(Long hostNo, Long roomNo, String status);

    // 동일한 입장 코드를 가진 방 개수 조회.
    Integer countRoomsByCode(String code);

    // 방 번호 기준 단일 방 조회.
    Room selectRoomByNo(Long no);
}
