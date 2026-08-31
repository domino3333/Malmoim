package com.malmoim.mapper;


import com.malmoim.domain.Room;
import com.malmoim.dto.qna.room.QnaRoomInfoResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoomMapper {


    // room 테이블에 방 추가
    void insertRoom(Room room);

    List<Room> selectRoomsByHostNo(@Param("hostNo") Long hostNo, @Param("offset") int offset, @Param("size") int size);

    Integer countRoomsByHostNo(@Param("hostNo") Long hostNo);

    Integer existsByRoomNoAndHostNo(@Param("roomNo") Long roomNo, @Param("hostNo") Long hostNo);

    // 방 번호와 호스트 번호가 일치하는 방 조회
    QnaRoomInfoResponse selectRoomByNoAndHostNo(@Param("roomNo") Long roomNo, @Param("hostNo") Long hostNo);



    // 동일한 입장 코드를 가진 방 개수 조회
    Integer countRoomsByCode(String code);

    Room selectRoomByCode(String code);

    // 방 번호 기준 단일 방 조회
    QnaRoomInfoResponse selectRoomByNo(@Param("roomNo") Long roomNo);
}
