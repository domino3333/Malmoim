package com.malmoim.service.qna;

import com.malmoim.domain.QnaPhase;
import com.malmoim.domain.QnaRoom;
import com.malmoim.mapper.QnaRoomMapper;
import com.malmoim.mapper.QuestionMapper;
import com.malmoim.mapper.VoteMapper;
import com.malmoim.service.qna.impl.VoteServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class VoteServiceImplTest {

    private final VoteMapper voteMapper = mock(VoteMapper.class);
    private final QnaRoomMapper qnaRoomMapper = mock(QnaRoomMapper.class);
    private final QuestionMapper questionMapper = mock(QuestionMapper.class);
    private final VoteServiceImpl voteService =
            new VoteServiceImpl(voteMapper, qnaRoomMapper, questionMapper);

    @Test
    void acceptsVoteAfterQuestionsCloseWhileVotingIsStillOpen() {
        LocalDateTime now = LocalDateTime.now();
        QnaRoom room = QnaRoom.builder()
                .roomNo(43L)
                .status(QnaPhase.VOTING_OPEN)
                .questionEndedAt(now.minusHours(1))
                .votingEndedAt(now.plusHours(1))
                .build();
        when(questionMapper.existsByRoomNoAndQuestionNo(43L, 10L)).thenReturn(1);
        when(qnaRoomMapper.selectQnaRoomByRoomNo(43L)).thenReturn(room);

        voteService.castVote(43L, 10L, 99L);

        verify(voteMapper).insertVote(10L, 99L);
        verify(questionMapper).incrementVoteCount(10L);
    }

    @Test
    void rejectsVoteAfterVotingDeadlineEvenBeforeSchedulerClosesPhase() {
        LocalDateTime now = LocalDateTime.now();
        QnaRoom room = QnaRoom.builder()
                .roomNo(43L)
                .status(QnaPhase.VOTING_OPEN)
                .questionEndedAt(now.minusHours(2))
                .votingEndedAt(now.minusHours(1))
                .build();
        when(questionMapper.existsByRoomNoAndQuestionNo(43L, 10L)).thenReturn(1);
        when(qnaRoomMapper.selectQnaRoomByRoomNo(43L)).thenReturn(room);

        assertThatThrownBy(() -> voteService.castVote(43L, 10L, 99L))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        error -> assertThat(error.getStatusCode()).isEqualTo(HttpStatus.CONFLICT));

        verifyNoInteractions(voteMapper);
        verify(questionMapper, never()).incrementVoteCount(anyLong());
    }

    @Test
    void duplicateVoteReturns409WithoutIncrementingCount() {
        allowVoting();
        doThrow(new DuplicateKeyException("vote_uk")).when(voteMapper).insertVote(10L, 99L);

        assertThatThrownBy(() -> voteService.castVote(43L, 10L, 99L))
                .isInstanceOfSatisfying(ResponseStatusException.class, error -> {
                    assertThat(error.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(error.getReason()).isNotBlank();
                });
        verify(questionMapper, never()).incrementVoteCount(anyLong());
    }

    @Test
    void unrelatedDatabaseFailureIsNotReportedAsDuplicateVote() {
        allowVoting();
        var failure = new DataIntegrityViolationException("foreign key failure");
        doThrow(failure).when(voteMapper).insertVote(10L, 99L);
        assertThatThrownBy(() -> voteService.castVote(43L, 10L, 99L)).isSameAs(failure);
        verify(questionMapper, never()).incrementVoteCount(anyLong());
    }

    @Test
    void missingQuestionReturns404WithoutSaving() {
        when(questionMapper.existsByRoomNoAndQuestionNo(43L, 10L)).thenReturn(0);
        assertThatThrownBy(() -> voteService.castVote(43L, 10L, 99L))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        error -> assertThat(error.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
        verifyNoInteractions(voteMapper);
    }

    @Test
    void missingQnaRoomReturns404WithoutSaving() {
        when(questionMapper.existsByRoomNoAndQuestionNo(43L, 10L)).thenReturn(1);
        assertThatThrownBy(() -> voteService.castVote(43L, 10L, 99L))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        error -> assertThat(error.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
        verifyNoInteractions(voteMapper);
    }

    private void allowVoting() {
        when(questionMapper.existsByRoomNoAndQuestionNo(43L, 10L)).thenReturn(1);
        when(qnaRoomMapper.selectQnaRoomByRoomNo(43L)).thenReturn(QnaRoom.builder()
                .roomNo(43L).status(QnaPhase.VOTING_OPEN)
                .votingEndedAt(LocalDateTime.now().plusHours(1)).build());
    }
}
