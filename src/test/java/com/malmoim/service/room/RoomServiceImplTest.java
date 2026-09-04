package com.malmoim.service.room;

import com.malmoim.domain.Member;
import com.malmoim.domain.Room;
import com.malmoim.dto.room.MyRoomsResponse;
import com.malmoim.mapper.MemberMapper;
import com.malmoim.mapper.RoomMapper;
import com.malmoim.service.room.impl.RoomServiceImpl;
import org.junit.jupiter.api.Test;

import java.beans.Introspector;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RoomServiceImplTest {

    @Test
    void getMyRoomsDoesNotExposeRoomPassword() throws Exception {
        RoomMapper roomMapper = mock(RoomMapper.class);
        MemberMapper memberMapper = mock(MemberMapper.class);
        RoomServiceImpl roomService = new RoomServiceImpl(roomMapper, memberMapper);

        Member host = Member.builder().no(7L).email("host@example.test").build();
        Room room = Room.builder()
                .no(43L)
                .hostNo(7L)
                .title("Q&A room")
                .code("ABC123")
                .capacity(30)
                .password("encoded-password")
                .type("QNA")
                .visibility("PRIVATE")
                .build();

        when(memberMapper.getMemberByEmail(host.getEmail())).thenReturn(host);
        when(roomMapper.selectRoomsByHostNo(host.getNo(), 0, 5)).thenReturn(List.of(room));
        when(roomMapper.countRoomsByHostNo(host.getNo())).thenReturn(1);

        MyRoomsResponse response = roomService.getMyRooms(host.getEmail(), 1, 5);
        Object roomItem = response.getRooms().get(0);
        Set<String> propertyNames = List.of(
                        Introspector.getBeanInfo(roomItem.getClass()).getPropertyDescriptors())
                .stream()
                .map(descriptor -> descriptor.getName())
                .collect(Collectors.toSet());

        assertThat(propertyNames).doesNotContain("password");
    }
}
