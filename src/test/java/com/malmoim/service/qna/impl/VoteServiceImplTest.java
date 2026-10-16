package com.malmoim.service.qna.impl;

import com.malmoim.domain.QnaPhase;
import com.malmoim.domain.QnaRoom;
import com.malmoim.mapper.QnaRoomMapper;
import com.malmoim.mapper.QuestionMapper;
import com.malmoim.mapper.VoteMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class VoteServiceImplTest {



    @Test
    @DisplayName("없는 질문을 추천하면 거절한다")
    void rejectsVoteWhenQuestionIsMissing() {

        QuestionMapper questionMapper = mock(QuestionMapper.class);
        when(questionMapper.selectQuestionNoForUpdate(10L, 43L)).thenReturn(null);

        VoteMapper voteMapper = mock(VoteMapper.class);
        QnaRoomMapper qnaRoomMapper = mock(QnaRoomMapper.class);

        VoteServiceImpl voteService = new VoteServiceImpl(voteMapper, qnaRoomMapper, questionMapper);

        ResponseStatusException error = assertThrows(ResponseStatusException.class, () -> voteService.castVote(10L, 43L, 10L));

        //기대한 statusCode와 일치하는지
        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());

        //실제로 voteMapper가 호출되지 않았는지 검사
        verifyNoInteractions(voteMapper);
    }


    @Test
    @DisplayName("마감시간이 지난 추천은 거절한다")
    void rejectsVoteAfterDeadline() {

        QuestionMapper questionMapper = mock(QuestionMapper.class);
        when(questionMapper.selectQuestionNoForUpdate(10L, 43L)).thenReturn(43L);

        QnaRoomMapper qnaRoomMapper = mock(QnaRoomMapper.class);

        //현재보다 한시간 전을 마감시간으로 준비
        LocalDateTime deadline = LocalDateTime.now().minusHours(1);

        QnaRoom room = QnaRoom.builder()
                .roomNo(10L)
                .status(QnaPhase.VOTING_OPEN)
                .votingEndsAt(deadline).
                build();

        when(qnaRoomMapper.selectQnaRoomByRoomNo(10L)).thenReturn(room);

        VoteMapper voteMapper = mock(VoteMapper.class);

        VoteServiceImpl voteService = new VoteServiceImpl(voteMapper, qnaRoomMapper, questionMapper);

        ResponseStatusException error = assertThrows(ResponseStatusException.class, () -> voteService.castVote(10L, 43L, 10L));

        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());

        verifyNoInteractions(voteMapper);

    }

    @Test
    @DisplayName("이미 추천한 질문을 다시 추천하면 거절")
    void rejectsDuplicateVote() {
        QuestionMapper questionMapper = mock(QuestionMapper.class);
        QnaRoomMapper qnaRoomMapper = mock(QnaRoomMapper.class);

        when(questionMapper.selectQuestionNoForUpdate(10L, 43L)).thenReturn(43L);

        LocalDateTime deadline = LocalDateTime.now().plusHours(1);
        QnaRoom room = QnaRoom.builder()
                .roomNo(10L)
                .votingEndsAt(deadline)
                .status(QnaPhase.VOTING_OPEN)
                .build();

        when(qnaRoomMapper.selectQnaRoomByRoomNo(10L)).thenReturn(room);

        VoteMapper voteMapper = mock(VoteMapper.class);

        doThrow(new DuplicateKeyException("중복 추천")).when(voteMapper).insertVote(43L, 10L);

        VoteServiceImpl voteService = new VoteServiceImpl(voteMapper, qnaRoomMapper, questionMapper);

        ResponseStatusException error = assertThrows(ResponseStatusException.class, () -> voteService.castVote(10L, 43L, 10L));

        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());

        //never = 호출횟수가 0이어야 함
        verify(questionMapper, never()).incrementVoteCount(43L);
    }

    @Test
    @DisplayName("추천 가능한 질문을 처음 추천하면 정상처리 한다")
    void acceptsFirstVote() {

        QuestionMapper questionMapper = mock(QuestionMapper.class);
        when(questionMapper.selectQuestionNoForUpdate(10L, 43L)).thenReturn(43L);

        LocalDateTime deadline = LocalDateTime.now().plusHours(1);

        QnaRoom room = QnaRoom.builder()
                .roomNo(10L)
                .votingEndsAt(deadline)
                .status(QnaPhase.VOTING_OPEN)
                .build();

        QnaRoomMapper qnaRoomMapper = mock(QnaRoomMapper.class);
        VoteMapper voteMapper = mock(VoteMapper.class);
        when(qnaRoomMapper.selectQnaRoomByRoomNo(10L)).thenReturn(room);

        VoteServiceImpl voteService = new VoteServiceImpl(voteMapper, qnaRoomMapper, questionMapper);

        voteService.castVote(10L, 43L, 10L);

        verify(voteMapper).insertVote(43L, 10L);
        verify(questionMapper).incrementVoteCount(43L);

    }


    // VOTING_OPEN 을 제외한 모든 name를 파라미터로 넘기는 테스트
    @ParameterizedTest
    @EnumSource(value = QnaPhase.class, names = "VOTING_OPEN", mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("마김시간이 남아 있어도 추천 단계가 아니면 거절한다")
    void rejectsVoteWhenVotingIsNotOpen(QnaPhase qnaPhase) {

        QuestionMapper questionMapper = mock(QuestionMapper.class);
        VoteMapper voteMapper = mock(VoteMapper.class);
        QnaRoomMapper qnaRoomMapper = mock(QnaRoomMapper.class);

        when(questionMapper.selectQuestionNoForUpdate(43L, 10L)).thenReturn(10L);

        LocalDateTime deadline = LocalDateTime.now().plusHours(1);

        QnaRoom qnaRoom = QnaRoom.builder()
                .roomNo(43L)
                .votingEndsAt(deadline)
                .status(qnaPhase)
                .build();

        when(qnaRoomMapper.selectQnaRoomByRoomNo(43L)).thenReturn(qnaRoom);

        VoteServiceImpl voteService = new VoteServiceImpl(voteMapper, qnaRoomMapper, questionMapper);


        ResponseStatusException error = assertThrows(ResponseStatusException.class, () -> voteService.castVote(43L, 10L, 10L));

        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());

        verify(voteMapper, never()).insertVote(10L, 10L);
        verify(questionMapper, never()).incrementVoteCount(10L);

    }

    @Test
    @DisplayName("방 정보를 찾지 못하면 추천을 거절한다")
    void rejectsVoteWhenQnaRoomIsMissing(){
        QuestionMapper questionMapper = mock(QuestionMapper.class);
        when(questionMapper.selectQuestionNoForUpdate(10L,43L)).thenReturn(43L);

        QnaRoomMapper qnaRoomMapper = mock(QnaRoomMapper.class);
        when(qnaRoomMapper.selectQnaRoomByRoomNo(10L)).thenReturn(null);

        VoteMapper voteMapper = mock(VoteMapper.class);

        VoteServiceImpl voteService = new VoteServiceImpl(voteMapper, qnaRoomMapper, questionMapper);

        ResponseStatusException error = assertThrows(ResponseStatusException.class,()->voteService.castVote(10L,43L,10L));

        assertEquals(HttpStatus.NOT_FOUND,error.getStatusCode());

        verify(voteMapper,never()).insertVote(43L,10L);

        verify(questionMapper,never()).incrementVoteCount(43L);

    }

}
