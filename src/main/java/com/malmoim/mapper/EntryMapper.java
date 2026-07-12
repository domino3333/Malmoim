package com.malmoim.mapper;


import com.malmoim.domain.Participant;
import com.malmoim.domain.Room;

public interface EntryMapper {

    Integer countRoomsByCode(String code);
    Room getRoomInfoByCode(String code);
    Room selectRoomByNo(Long roomNo);
    void insertParticipant(Participant participant);
}
