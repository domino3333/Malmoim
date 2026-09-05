package com.malmoim.controller.qna;

import com.malmoim.dto.qna.presence.ParticipantInfoResponse;
import com.malmoim.dto.qna.presence.ParticipantPresenceResponse;
import com.malmoim.dto.qna.question.QuestionResponse;
import com.malmoim.dto.qna.room.QnaRoomInfoResponse;
import com.malmoim.security.ParticipantPrincipal;
import com.malmoim.service.qna.QnaPresenceService;
import com.malmoim.service.qna.QnaRoomService;
import com.malmoim.service.qna.QuestionService;
import com.malmoim.service.qna.VoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/participant/qna")
public class ParticipantQnaController {

    private final QnaRoomService qnaRoomService;
    private final QnaPresenceService qnaPresenceService;
    private final QuestionService questionService;
    private final VoteService voteService;

    // 참가자가 입장한 Q&A 방 정보 조회
    @GetMapping("/{roomNo}/participant")
    public ResponseEntity<?> getParticipantQnaRoom(Authentication authentication, @PathVariable Long roomNo) {

        ParticipantPrincipal participant = (ParticipantPrincipal) authentication.getPrincipal();
        QnaRoomInfoResponse room = qnaRoomService.getParticipantQnaRoom(participant.getParticipantNo(),participant.getRoomNo());

        return ResponseEntity.ok(room);
    }

    // 참여자 정보를 내려줌
    @GetMapping("/participant-info")
    public ResponseEntity<?> getParticipantInfo(Authentication authentication) {
        ParticipantPrincipal participant = (ParticipantPrincipal) authentication.getPrincipal();

        Long participantNo = participant.getParticipantNo();
        log.info("participantNo:{}", participantNo);
        String nickname = participant.getNickname();

        ParticipantInfoResponse response = new ParticipantInfoResponse(nickname);

        return ResponseEntity.ok(response);
    }

    //참여자 명단과 인원 수를 내려줌(http스냅샷)
    @GetMapping("/participant-list")
    public ResponseEntity<?> getParticipantList(Authentication authentication) {

        ParticipantPrincipal participant = (ParticipantPrincipal) authentication.getPrincipal();
        Long roomNo = participant.getRoomNo();
        ParticipantPresenceResponse response = qnaPresenceService.getActiveParticipantSnapshot(roomNo);

        return ResponseEntity.ok(response);
    }

    //질문 리스트(http스냅샷)
    @GetMapping("/question-list")
    public ResponseEntity<?> getQuestionList(Authentication authentication) {

        ParticipantPrincipal participant = (ParticipantPrincipal) authentication.getPrincipal();
        Long roomNo = participant.getRoomNo();
        List<QuestionResponse> response = questionService.getQuestionList(roomNo);


        return ResponseEntity.ok(response);
    }

    //참여자가 좋아요 버튼을 눌렀을 때의 api
    @PostMapping("/{questionNo}/vote-question")
    public ResponseEntity<?> castVote(Authentication authentication, @PathVariable long questionNo) {

        //todo 좋아요 버튼을 눌렀을때 http로는 줄거있나?
        //다시 돌려줄건 없을거같고, 즉시 db에 좋아요 테이블에 insert만 해주면 될거같음
        // 웹소켓으로는 실시간으로 오르내리는 좋아요 수를 보여줘야하는데
        /*
        CONSTRAINT vote_uk
        UNIQUE (participant_no, question_no) 이기때문에 한 사용자는 하나의 질문에만 좋아요 클릭 가능
        */
        ParticipantPrincipal participant = (ParticipantPrincipal) authentication.getPrincipal();
        Long roomNo = participant.getRoomNo();

        voteService.castVote(roomNo, questionNo, participant.getParticipantNo());


        return ResponseEntity.ok(null);
    }

}
