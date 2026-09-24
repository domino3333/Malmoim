package com.malmoim.controller.room;


import com.malmoim.dto.room.MyRoomResponse;
import com.malmoim.dto.room.MyRoomsResponse;
import com.malmoim.service.room.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/room")
public class RoomController {


    private final RoomService roomService;

    @GetMapping("/recent-rooms")
    public ResponseEntity<List<MyRoomResponse>> getRecentRooms(Authentication authentication) {
        List<MyRoomResponse> rooms = roomService.getMyRecentRooms(authentication.getName());
        return ResponseEntity.ok(rooms);
    }


    @GetMapping
    public ResponseEntity<?> getMyRooms(Authentication authentication, @RequestParam int page, @RequestParam int size) {


        String hostEmail = authentication.getName();
        MyRoomsResponse dto = roomService.getMyRooms(hostEmail, page, size);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchRoom(Authentication authentication, @RequestParam String keyword, @RequestParam int page, @RequestParam int size) {

        log.info("searchRoom진입");

        String hostEmail = authentication.getName();
        MyRoomsResponse dto = roomService.getSearchedRooms(hostEmail, keyword, page, size);

        return ResponseEntity.ok(dto);
    }

}
