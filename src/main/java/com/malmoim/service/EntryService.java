package com.malmoim.service;

import com.malmoim.dto.entry.CheckCodeResponse;

public interface EntryService {
    CheckCodeResponse checkRoomCode(String code);
}
