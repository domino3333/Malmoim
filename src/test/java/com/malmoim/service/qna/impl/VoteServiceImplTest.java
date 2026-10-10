package com.malmoim.service.qna.impl;

import com.malmoim.domain.QnaPhase;
import com.malmoim.domain.QnaRoom;
import com.malmoim.mapper.QnaRoomMapper;
import com.malmoim.mapper.QuestionMapper;
import com.malmoim.mapper.VoteMapper;
import com.malmoim.service.qna.impl.VoteServiceImpl;
import org.apache.coyote.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cglib.core.Local;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class VoteServiceImplTest {


    //근데 그냥 임의의 값을 내가 지정해서 그걸 돌려줘라고하는거면 이런 mock은 왜필요한거지? 그냥 0을 넣으면 되잖아


    @Test
    @DisplayName("없는 질문을 추천하면 거절한다")
    void requestsMissingQuestion() {

        QuestionMapper questionMapper = mock(QuestionMapper.class);
        when(questionMapper.existsByRoomNoAndQuestionNo(10L, 43L)).thenReturn(0);

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
        when(questionMapper.existsByRoomNoAndQuestionNo(10L, 43L)).thenReturn(1);

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

        assertEquals(HttpStatus.CONFLICT,error.getStatusCode());

        verifyNoInteractions(voteMapper);

    }

    @Test
    @DisplayName("이미 추천한 질문을 다시 추천하면 거절")
    void rejectsDuplicateVote(){
        QuestionMapper questionMapper = mock(QuestionMapper.class);
        QnaRoomMapper qnaRoomMapper = mock(QnaRoomMapper.class);

        when(questionMapper.existsByRoomNoAndQuestionNo(10L,43L)).thenReturn(1);

        LocalDateTime deadline = LocalDateTime.now().plusHours(1);
        QnaRoom room = QnaRoom.builder()
                .roomNo(10L)
                .votingEndsAt(deadline)
                .status(QnaPhase.VOTING_OPEN)
                .build();

        when(qnaRoomMapper.selectQnaRoomByRoomNo(10L)).thenReturn(room);

        VoteMapper voteMapper = mock(VoteMapper.class);

        doThrow(new DuplicateKeyException("중복 추천")).when(voteMapper).insertVote(43L,10L);

        VoteServiceImpl voteService = new VoteServiceImpl(voteMapper, qnaRoomMapper, questionMapper);

        ResponseStatusException error = assertThrows(ResponseStatusException.class,()->voteService.castVote(10L,43L,10L));

        assertEquals(HttpStatus.CONFLICT,error.getStatusCode());

        //never = 호출횟수가 0이어야 함
        verify(questionMapper,never()).incrementVoteCount(43L);
    }

    @Test
    @DisplayName("추천 가능한 질문을 처음 추천하면 정상처리 한다")
    void acceptsFirstVote(){

        QuestionMapper questionMapper = mock(QuestionMapper.class);
        when(questionMapper.existsByRoomNoAndQuestionNo(10L,43L)).thenReturn(1);

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

        voteService.castVote(10L,43L,10L);

        verify(voteMapper).insertVote(43L,10L);
        verify(questionMapper).incrementVoteCount(43L);

    }


    @Test
    @DisplayName("마김시간이 남아 있어도 추천 단계가 아니면 거절한다")
    void rejectsVoteWhenVotingIsNotOpen(){

        QuestionMapper questionMapper = mock(QuestionMapper.class);
        VoteMapper voteMapper = mock(VoteMapper.class);
        QnaRoomMapper qnaRoomMapper = mock(QnaRoomMapper.class);

        when(questionMapper.existsByRoomNoAndQuestionNo(43L,10L)).thenReturn(1);

        LocalDateTime deadline = LocalDateTime.now().plusHours(1);

        QnaRoom qnaRoom = QnaRoom.builder()
                .roomNo(43L)
                .votingEndsAt(deadline)
                //qna페이즈의 votingopen을 반대로 걸어야하는데
                .status(QnaPhase.QUESTION_CLOSED)
                .build();

        when(qnaRoomMapper.selectQnaRoomByRoomNo(43L)).thenReturn(qnaRoom);

        VoteServiceImpl voteService = new VoteServiceImpl(voteMapper, qnaRoomMapper, questionMapper);


        ResponseStatusException error = assertThrows(ResponseStatusException.class,()->voteService.castVote(43L,10L,10L));

        assertEquals(HttpStatus.CONFLICT,error.getStatusCode());

        verify(voteMapper).insertVote(10L,10L);
        verify(questionMapper).incrementVoteCount(10L);

    }

}
