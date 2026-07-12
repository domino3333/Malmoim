package com.malmoim.mapper;


import com.malmoim.domain.Participant;
import com.malmoim.domain.Room;

public interface EntryMapper {

    // 동일한 입장 코드를 가진 방 개수 조회.
    Integer countRoomsByCode(String code);
    Room getRoomInfoByCode(String code);
    // 방 번호 기준 입장 대상 방 조회.
    Room selectRoomByNo(Long roomNo);
    void insertParticipant(Participant participant);
}
