package com.malmoim.controller.room;

import com.malmoim.dto.room.MyRoomResponse;
import com.malmoim.service.room.RoomService;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RecentRoomsControllerTest {

    @Test
    void recentRoomsRouteReturnsRoomNumbersForAuthenticatedHost() throws Exception {
        RoomService roomService = mock(RoomService.class);
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new RoomController(roomService)).build();
        MyRoomResponse room = MyRoomResponse.builder().roomNo(43L).title("Test room").build();
        when(roomService.getMyRecentRooms("host@example.test")).thenReturn(List.of(room));

        mvc.perform(get("/api/room/recent-rooms").principal(
                        new UsernamePasswordAuthenticationToken("host@example.test", null, List.of())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].roomNo").value(43))
                .andExpect(jsonPath("$[0].title").value("Test room"));

        verify(roomService).getMyRecentRooms("host@example.test");
    }
}
