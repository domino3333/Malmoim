package com.malmoim.mapper;


import com.malmoim.domain.Room;

public interface EntryMapper {

    Integer countRoomByCode(String code);
    Room getRoomInfoByCode(String code);


}
