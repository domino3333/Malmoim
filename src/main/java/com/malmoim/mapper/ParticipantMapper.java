package com.malmoim.mapper;

import com.malmoim.domain.Participant;
import org.apache.ibatis.annotations.Param;

public interface ParticipantMapper {

    void insertParticipant(Participant participant);

    Integer existsByParticipantNoAndRoomNo(
            @Param("participantNo") Long participantNo,
            @Param("roomNo") Long roomNo
    );
}
