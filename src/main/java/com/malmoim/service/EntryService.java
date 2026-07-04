package com.malmoim.service;

import com.malmoim.dto.entry.CheckCodeResponse;
import com.malmoim.dto.entry.CheckPasswordResponse;

public interface EntryService {
    CheckCodeResponse checkRoomCode(String code);

    CheckPasswordResponse checkRoomPassword(String password);
}
