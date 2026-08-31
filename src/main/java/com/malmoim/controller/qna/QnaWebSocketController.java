package com.malmoim.controller.qna;


import com.malmoim.dto.qna.question.QuestionCreatedMessage;
import com.malmoim.dto.qna.question.SubmitQuestionMessage;
import com.malmoim.security.ParticipantPrincipal;
import com.malmoim.service.qna.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class QnaWebSocketController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final QuestionService questionService;


    @MessageMapping("/qna/register")
    // 참여자가 보낸 질문을 같은 방의 모든 구독자에게 전달
    public void registerAndBroadcastQuestion(SubmitQuestionMessage dto, Authentication authentication) {


        //참여자가 보낸 질문 로그로 테스트
        log.info("registerAndBroadcastQuestion 함수로 들어온 question:{} ", dto.getQuestion());
        log.info("registerAndBroadcastQuestion 함수로 들어온 roomNo:{}", dto.getRoomNo());

        ParticipantPrincipal participant = (ParticipantPrincipal) authentication.getPrincipal();

        Long participantNo = participant.getParticipantNo();
        Long roomNo = participant.getRoomNo();
        String nickname = participant.getNickname();


        QuestionCreatedMessage message =
        questionService.createQuestion(roomNo,participantNo,dto.getQuestion(),nickname);

        simpMessagingTemplate.convertAndSend("/topic/qna/" + roomNo, message);

    }


}
