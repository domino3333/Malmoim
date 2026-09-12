package com.malmoim.service.qna;

import com.malmoim.domain.QnaPhase;
import com.malmoim.domain.QnaRoom;
import com.malmoim.mapper.QnaRoomMapper;
import com.malmoim.mapper.QuestionMapper;
import com.malmoim.mapper.VoteMapper;
import com.malmoim.service.qna.impl.VoteServiceImpl;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        when(questionMapper.isExistsQuestionInTheRoom(43L, 10L)).thenReturn(1);
        when(qnaRoomMapper.selectQnaRoomByRoomNo(43L)).thenReturn(room);

        voteService.castVote(43L, 10L, 99L);

        verify(voteMapper).castVote(10L, 99L);
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
        when(questionMapper.isExistsQuestionInTheRoom(43L, 10L)).thenReturn(1);
        when(qnaRoomMapper.selectQnaRoomByRoomNo(43L)).thenReturn(room);

        assertThatThrownBy(() -> voteService.castVote(43L, 10L, 99L))
                .isInstanceOf(RuntimeException.class);

        verifyNoInteractions(voteMapper);
        verify(questionMapper, never()).incrementVoteCount(anyLong());
    }
}
