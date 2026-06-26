package com.malmoim.controller;


import com.malmoim.domain.Member;
import com.malmoim.domain.Room;
import com.malmoim.dto.room.MyRoomsResponseDto;
import com.malmoim.dto.room.qna.CreateQnaRoomDto;
import com.malmoim.service.QnaRoomService;
import com.malmoim.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/qna")
public class QnaController {


    private final RoomService roomService;
    private final QnaRoomService qnaRoomService;

    @PostMapping("/create")
    public ResponseEntity<?> createQnARoom(Authentication authentication, @RequestBody CreateQnaRoomDto dto){

        String hostEmail = authentication.getName();
        log.info("ischecked:{}",dto.getIsChecked());
        roomService.createQnARoom(dto,hostEmail);

        return ResponseEntity.ok("방 생성 완료");
    }

    @GetMapping("/{no}")
    public ResponseEntity<?> getMyOneQnaRoom(Authentication authentication,@PathVariable Long no){

        //todo 시작시간, 종료시간 보여줄거면 room과 qna_room을 조인해서 보여주기
        String hostEmail = authentication.getName();
        Room room = roomService.getMyOneRoom(no,hostEmail);

        return ResponseEntity.ok(room);
    }


    @PostMapping("/{roomNo}/start-timer")
    public ResponseEntity<?> startTimer(Authentication authentication, @RequestBody long durationSeconds,@PathVariable long roomNo){

        String hostEmail = authentication.getName();
        qnaRoomService.updateQuestionStartedAt(hostEmail,durationSeconds,roomNo);


        return null;
    }


}
