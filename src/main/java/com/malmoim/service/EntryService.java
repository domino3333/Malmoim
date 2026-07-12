package com.malmoim.service;

import com.malmoim.dto.entry.*;

public interface EntryService {
    CheckCodeResponse getRoomEntryInfo(String code);

    CheckPasswordResponse verifyRoomPassword(CheckPasswordRequest dto);

    InsertParticipantResponse joinRoom(InsertParticipantRequest dto);
}
