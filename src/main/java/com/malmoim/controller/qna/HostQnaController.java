package com.malmoim.controller.qna;

import com.malmoim.domain.Room;
import com.malmoim.dto.qna.CreateQnaRoomRequest;
import com.malmoim.dto.qna.ParticipantListCountResponse;
import com.malmoim.dto.qna.timer.StartTimerRequest;
import com.malmoim.dto.qna.timer.StartTimerResponse;
import com.malmoim.dto.qna.timer.UpdateRoomStatusRequest;
import com.malmoim.security.ParticipantPrincipal;
import com.malmoim.service.qna.QnaPresenceService;
import com.malmoim.service.qna.QnaRoomService;
import com.malmoim.service.room.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/host/qna")
public class HostQnaController {

    private final RoomService roomService;
    private final QnaRoomService qnaRoomService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final QnaPresenceService qnaPresenceService;

    @PostMapping("/create")
    public ResponseEntity<?> createQnaRoom(Authentication authentication, @RequestBody CreateQnaRoomRequest dto) {
        String hostEmail = authentication.getName();
        qnaRoomService.createQnaRoom(dto, hostEmail);

        return ResponseEntity.ok("방 생성 완료");
    }

    @GetMapping("/{no}/host")
    // 호스트 소유의 Q&A 방 정보 조회
    public ResponseEntity<?> getHostQnaRoom(Authentication authentication, @PathVariable Long no) {
        //todo 시작시간, 종료시간 보여줄거면 room과 qna_room을 조인해서 보여주기
        String hostEmail = authentication.getName();
        Room room = roomService.getOwnedRoomByNo(no, hostEmail);

        return ResponseEntity.ok(room);
    }

    @PostMapping("/{roomNo}/start-timer")
    // 호스트가 설정한 시간으로 질문 접수 단계 시작
    public ResponseEntity<?> startQuestionPhase(Authentication authentication, @RequestBody StartTimerRequest dto, @PathVariable long roomNo) {
        String hostEmail = authentication.getName();
        StartTimerResponse response = qnaRoomService.startQuestionPhase(hostEmail, dto.getDurationSeconds(), roomNo);

        // 타이머를 시작하면 프론트에 웹소켓으로 알람을 보내주기
        simpMessagingTemplate.convertAndSend("/topic/qna/" + roomNo + "/phase", response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{roomNo}/update-status")
    public ResponseEntity<?> updateRoomStatus(Authentication authentication, @PathVariable long roomNo, @RequestBody UpdateRoomStatusRequest request) {
        String hostEmail = authentication.getName();
        qnaRoomService.updateRoomStatus(hostEmail, roomNo, request.getStatus());

        return ResponseEntity.ok("업데이트 완료");
    }

    //참여자 명단과 인원 수를 내려줌(http스냅샷)
    @GetMapping("/{roomNo}/participant-list")
    public ResponseEntity<?> getParticipantList(Authentication authentication,@PathVariable long roomNo) {

        String hostEmail = authentication.getName();

        // 이 roomNo를 실제로 hostNo가 갖고 있는지 판단
        boolean isHostsRoom = qnaRoomService.validateRoomOwnership(roomNo, hostEmail);
        ParticipantListCountResponse response = qnaPresenceService.getActiveParticipantSnapshot(roomNo);

        if(isHostsRoom){
            return ResponseEntity.ok(response);
        }else{
            return ResponseEntity.ok("hostNo에 해당하는 방이 없습니다.");
        }

    }
}
