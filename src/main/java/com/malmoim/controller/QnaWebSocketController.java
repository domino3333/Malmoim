package com.malmoim.controller;


import com.malmoim.dto.room.qna.QnaQuestionMessage;
import com.malmoim.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class QnaWebSocketController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final QuestionService questionService;


    @MessageMapping("/qna/register")
    // 참여자가 보낸 질문을 같은 방의 모든 구독자에게 전달.
    public void broadcastQuestion(QnaQuestionMessage dto) {

        //참여자가 보낸 질문 로그로 테스트
        log.info("websocket server dto:{} ", dto.getQuestion());
        log.info("websocket server dto:{} ", dto.getRoomNo());

        questionService.registerQuestion(dto.getRoomNo(),dto.getQuestion());

        simpMessagingTemplate.convertAndSend("/topic/qna/" + dto.getRoomNo(), dto);

    }


}
