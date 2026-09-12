package com.malmoim.scheduler.qna;

import com.malmoim.dto.qna.phase.QnaPhaseResponse;
import com.malmoim.service.qna.QnaRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class QnaPhaseScheduler {

    private final QnaRoomService qnaRoomService;
    private final SimpMessagingTemplate simpMessagingTemplate;


    @Scheduled(fixedDelay = 1000)
    public void closeExpiredQnaPhases(){
        List<QnaPhaseResponse> closedPhases = qnaRoomService.closeExpiredPhases();

        for(QnaPhaseResponse response:closedPhases){

            simpMessagingTemplate.convertAndSend("/topic/qna/"+response.getRoomNo()+"/phase",response);

            log.info("스케쥴러에 의해 qna room 자동 종료된 방:{}",response.getRoomNo());
        }
    }

}
