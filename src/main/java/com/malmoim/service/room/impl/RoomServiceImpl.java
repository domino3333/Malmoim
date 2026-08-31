package com.malmoim.service.room.impl;

import com.malmoim.domain.Member;
import com.malmoim.dto.room.MyRoomsResponse;
import com.malmoim.mapper.MemberMapper;
import com.malmoim.mapper.RoomMapper;
import com.malmoim.service.room.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomMapper roomMapper;
    private final MemberMapper memberMapper;

    @Override
    @Transactional(readOnly = true)
    public MyRoomsResponse getMyRooms(String hostEmail, int page, int size) {
        Member host = memberMapper.getMemberByEmail(hostEmail);

        MyRoomsResponse dto = new MyRoomsResponse();
        int offset = (page - 1) * size;

        dto.setRooms(roomMapper.selectRoomsByHostNo(host.getNo(), offset, size));
        dto.setTotalCount(roomMapper.countRoomsByHostNo(host.getNo()));

        return dto;
    }
}
