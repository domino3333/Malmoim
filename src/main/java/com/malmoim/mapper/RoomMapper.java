package com.malmoim.mapper;


import com.malmoim.domain.Room;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoomMapper {


    void createRoom(Room room);

    List<Room> getMyRooms(@Param("no") Long no,@Param("offset") int offset,@Param("size") int size);

    Integer countMyRooms(Long no);

    Room getMyOneRoom(Long no, Long hostNo);

    void updateRoomStatus(Long hostNo, Long roomNo, String status);

    Integer countByCode(String code);

    Room getOneRoomWithOnlyNo(Long no);
}
