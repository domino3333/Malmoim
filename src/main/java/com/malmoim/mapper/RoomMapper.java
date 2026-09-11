package com.malmoim.mapper;


import com.malmoim.domain.Room;
import com.malmoim.dto.room.MyRoomResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoomMapper {


    // room 테이블에 방 추가
    void insertRoom(Room room);

    List<MyRoomResponse> selectRoomsByHostNo(@Param("hostNo") Long hostNo, @Param("offset") int offset, @Param("size") int size);

    Integer countRoomsByHostNo(@Param("hostNo") Long hostNo);

    Integer existsByRoomNoAndHostNo(@Param("roomNo") Long roomNo, @Param("hostNo") Long hostNo);



    // 동일한 입장 코드를 가진 방 개수 조회
    Integer countRoomsByCode(String code);

    Room selectRoomByCode(String code);

    Room selectRoomForPasswordVerification(Long roomNo);



}
