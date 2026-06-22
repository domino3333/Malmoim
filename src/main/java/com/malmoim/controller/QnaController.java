package com.malmoim.controller;


import com.malmoim.domain.Room;
import com.malmoim.dto.room.MyRoomsResponseDto;
import com.malmoim.dto.room.qna.CreateQnaRoomDto;
import com.malmoim.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/qna")
public class QnaController {


    private final RoomService roomService;

    @PostMapping("/create")
    public ResponseEntity<?> createQnARoom(Authentication authentication, @RequestBody CreateQnaRoomDto dto){

        String hostEmail = authentication.getName();
        log.info("ischecked:{}",dto.getIsChecked());
        roomService.createQnARoom(dto,hostEmail);

        return ResponseEntity.ok("방 생성 완료");
    }

    @GetMapping("/{no}")
    public ResponseEntity<?> getMyOneRoom(Authentication authentication,@PathVariable Long no){

        String hostEmail = authentication.getName();
        Room room = roomService.getMyOneRoom(no,hostEmail);

        return ResponseEntity.ok(room);
    }


}
