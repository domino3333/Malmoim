package com.malmoim.controller.qna;

import com.malmoim.domain.QnaPhase;
import com.malmoim.dto.qna.*;
import com.malmoim.dto.qna.timer.StartQnaPhaseRequest;
import com.malmoim.dto.qna.timer.UpdateRoomStatusRequest;
import com.malmoim.service.qna.QnaPresenceService;
import com.malmoim.service.qna.QnaRoomService;
import com.malmoim.service.qna.QuestionService;
import com.malmoim.service.room.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/host/qna")
@Slf4j
public class HostQnaController {

    private final RoomService roomService;
    private final QnaRoomService qnaRoomService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final QnaPresenceService qnaPresenceService;
    private final QuestionService questionService;

    @PostMapping("/create")
    public ResponseEntity<?> createQnaRoom(Authentication authentication, @RequestBody CreateQnaRoomRequest dto) {
        String hostEmail = authentication.getName();
        qnaRoomService.createQnaRoom(dto, hostEmail);

        return ResponseEntity.ok("방 생성 완료");
    }

    // 호스트 소유의 Q&A 방 정보 조회
    @GetMapping("/{roomNo}/host")
    public ResponseEntity<?> getHostQnaRoom(Authentication authentication, @PathVariable Long roomNo) {
        String hostEmail = authentication.getName();
        QnaRoomInfoResponse room = roomService.getOwnedRoomByNo(roomNo, hostEmail);

        return ResponseEntity.ok(room);
    }

    // 호스트가 설정한 시간으로 질문 접수 단계 시작
    @PostMapping("/{roomNo}/start-timer")
    public ResponseEntity<?> startQuestionPhase(Authentication authentication, @RequestBody @Valid StartQnaPhaseRequest dto, @PathVariable long roomNo) {

        String hostEmail = authentication.getName();

        QnaPhaseResponse response = qnaRoomService.startQuestionPhase(hostEmail, dto.getDurationSeconds(), roomNo);

        // 타이머를 시작하면 프론트에 웹소켓으로 알람을 보내주기
        simpMessagingTemplate.convertAndSend("/topic/qna/" + roomNo + "/phase", response);

        return ResponseEntity.ok(response);
    }

    // 호스트가 설정한 시간으로 투표 페이즈 시작
    @PostMapping("/{roomNo}/start-voting")
    public ResponseEntity<?> startVotingPhase(Authentication authentication, @RequestBody @Valid StartQnaPhaseRequest dto, @PathVariable long roomNo) {

        String hostEmail = authentication.getName();

        QnaPhaseResponse response = qnaRoomService.startVotingPhase(hostEmail, dto.getDurationSeconds(), roomNo);

        // 타이머를 시작하면 프론트에 웹소켓으로 알람을 보내주기
        simpMessagingTemplate.convertAndSend("/topic/qna/" + roomNo + "/phase", response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{roomNo}/update-status")
    public ResponseEntity<?> updateRoomStatus(Authentication authentication, @PathVariable long roomNo, @RequestBody UpdateRoomStatusRequest request) {
        String hostEmail = authentication.getName();
        QnaPhaseResponse response = qnaRoomService.updateRoomStatus(hostEmail, roomNo, request.getStatus());

        simpMessagingTemplate.convertAndSend("/topic/qna/" + roomNo + "/phase", response);

        return ResponseEntity.ok(response);
    }

    //참여자 명단과 인원 수를 내려줌(http스냅샷)
    @GetMapping("/{roomNo}/participant-list")
    public ResponseEntity<?> getParticipantList(Authentication authentication, @PathVariable long roomNo) {

        String hostEmail = authentication.getName();

        // host의 방 소유권 검사
        boolean isHostsRoom = qnaRoomService.validateRoomOwnership(roomNo, hostEmail);
        ParticipantListCountResponse response = qnaPresenceService.getActiveParticipantSnapshot(roomNo);

        if (isHostsRoom) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("해당 방에 대한 권한이 없습니다.");

        }
    }

    //질문 리스트(http스냅샷)
    @GetMapping("/{roomNo}/question-list")
    public ResponseEntity<?> getQuestionList(Authentication authentication, @PathVariable long roomNo) {

        String hostEmail = authentication.getName();

        // host의 방 소유권 검사
        boolean isHostsRoom = qnaRoomService.validateRoomOwnership(roomNo, hostEmail);
        List<QuestionListResponse> response = questionService.getQuestionList(roomNo);

        if (isHostsRoom) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("해당 방에 대한 권한이 없습니다.");

        }

    }

    @PostMapping("/{roomNo}/start-answering")
    public ResponseEntity<?> startAnswering(Authentication authentication, @PathVariable long roomNo) {

        String hostEmail = authentication.getName();

        // host의 방 소유권 검사
        boolean isHostsRoom = qnaRoomService.validateRoomOwnership(roomNo, hostEmail);

        if (!isHostsRoom) {
            throw new RuntimeException("방에 대한 권한이 없습니다.");
        }
        QnaPhaseResponse response = qnaRoomService.updateRoomStatus(hostEmail, roomNo, QnaPhase.ANSWERING);

        //ANSWERING 상태가 되었다고 알람을 보내주기
        simpMessagingTemplate.convertAndSend("/topic/qna/"+roomNo+"/phase",response);

        return ResponseEntity.ok(response);


    }

}
