package com.malmoim.service.room.impl;

import com.malmoim.domain.Member;
import com.malmoim.dto.room.MyRoomResponse;
import com.malmoim.dto.room.MyRoomsResponse;
import com.malmoim.mapper.MemberMapper;
import com.malmoim.mapper.RoomMapper;
import com.malmoim.service.room.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomMapper roomMapper;
    private final MemberMapper memberMapper;

    @Override
    @Transactional(readOnly = true)
    public void validateRoomOwnership(long roomNo, String hostEmail) {
        Member host = memberMapper.getMemberByEmail(hostEmail);

        if (host == null || roomMapper.existsByRoomNoAndHostNo(roomNo, host.getNo()) != 1) {
            throw new AccessDeniedException("해당 방에 대한 권한이 없습니다.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public MyRoomsResponse getMyRooms(String hostEmail, int page, int size) {
        Member host = memberMapper.getMemberByEmail(hostEmail);

        MyRoomsResponse dto = new MyRoomsResponse();
        int offset = (page - 1) * size;

        List<MyRoomResponse> rooms = roomMapper.selectRoomsByHostNo(host.getNo(), offset, size);

        dto.setRooms(rooms);
        dto.setTotalCount(roomMapper.countRoomsByHostNo(host.getNo()));

        return dto;
    }
}
