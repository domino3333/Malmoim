package com.malmoim.mapper;


import com.malmoim.domain.Participant;
import com.malmoim.domain.Room;

public interface EntryMapper {

    Integer countRoomByCode(String code);
    Room getRoomInfoByCode(String code);
    Room getOneRoomWithOnlyRoomNo(Long roomNo);
    void insertParticipant(Participant participant);
}
