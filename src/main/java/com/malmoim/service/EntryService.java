package com.malmoim.service;

import com.malmoim.dto.entry.*;

public interface EntryService {
    // 입장 코드 기반 참가자용 방 정보 조회.
    CheckCodeResponse getRoomEntryInfo(String code);

    // 입력한 방 비밀번호의 일치 여부 검증.
    CheckPasswordResponse verifyRoomPassword(CheckPasswordRequest dto);

    // 참가자 생성 및 방 입장 처리.
    InsertParticipantResponse joinRoom(InsertParticipantRequest dto);
}
