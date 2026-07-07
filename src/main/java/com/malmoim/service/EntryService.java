package com.malmoim.service;

import com.malmoim.dto.entry.*;

public interface EntryService {
    CheckCodeResponse checkRoomCode(String code);

    CheckPasswordResponse checkRoomPassword(CheckPasswordRequest dto);

    InsertParticipantResponse insertParticipant(InsertParticipantRequest dto);
}
