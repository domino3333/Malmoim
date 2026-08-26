package com.malmoim.service.room.impl;

import com.malmoim.domain.Member;
import com.malmoim.domain.Room;
import com.malmoim.dto.qna.QnaRoomInfoResponse;
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

        dto.setRooms(roomMapper.getMyRooms(host.getNo(), offset, size));
        dto.setTotalCount(roomMapper.countMyRooms(host.getNo()));

        return dto;
    }

    @Override
    // 로그인한 호스트 소유의 방 조회
    public QnaRoomInfoResponse getOwnedRoomByNo(long roomNo, String hostEmail) {
        Member host = memberMapper.getMemberByEmail(hostEmail);

        return roomMapper.selectRoomByNoAndHostNo(roomNo, host.getNo());
    }

    @Override
    // 방 번호 기준 단일 방 조회
    public QnaRoomInfoResponse getRoomByNo(Long roomNo) {
        return roomMapper.selectRoomByNo(roomNo);
    }
}
