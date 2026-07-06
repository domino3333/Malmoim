package com.malmoim.service;

import com.malmoim.dto.entry.*;

public interface EntryService {
    CheckCodeResponse checkRoomCode(String code);

    CheckPasswordResponse checkRoomPassword(CheckPasswordRequest dto);

    InsertNickResponse insertNickname(InsertNickRequest dto);
}
