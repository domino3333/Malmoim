package com.malmoim.service.impl;

import com.malmoim.domain.Room;
import com.malmoim.dto.room.CreateQnaRoomDto;
import com.malmoim.mapper.RoomMapper;
import com.malmoim.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomMapper roomMapper;


    @Override
    public void createQnARoom(CreateQnaRoomDto dto, String hostEmail) {

        //코드 발급,패스워드 null여부

        Room.builder()
                .title(dto.getTitle())
                .capacity(dto.getCapacity())
                .password(dto.getPassword())
                .type("QnA")
                .status("opened")
                .build();
    }
}
