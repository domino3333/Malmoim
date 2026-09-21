package com.malmoim.service.qna;

import com.malmoim.domain.Question;
import com.malmoim.domain.QuestionStatus;
import com.malmoim.dto.qna.question.ToggleAnswerStatusResponse;
import com.malmoim.mapper.QnaRoomMapper;
import com.malmoim.mapper.QuestionMapper;
import com.malmoim.service.qna.impl.QuestionServiceImpl;
import com.malmoim.service.room.RoomService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class QuestionServiceImplTest {

    private final QuestionMapper questionMapper = mock(QuestionMapper.class);
    private final QnaRoomMapper qnaRoomMapper = mock(QnaRoomMapper.class);
    private final RoomService roomService = mock(RoomService.class);
    private final QuestionServiceImpl questionService =
            new QuestionServiceImpl(questionMapper, qnaRoomMapper, roomService);

    @Test
    void togglesWaitingQuestionUsingCurrentDatabaseStatus() {
        Question question = Question.builder()
                .no(10L)
                .roomNo(43L)
                .status("WAITING")
                .build();

        when(questionMapper.existsByRoomNoAndQuestionNo(43L, 10L)).thenReturn(1);
        when(questionMapper.selectQuestionByQuestionNo(10L)).thenReturn(question);
        when(questionMapper.updateQuestionStatus(10L, QuestionStatus.ANSWERED)).thenReturn(1);

        ToggleAnswerStatusResponse response =
                questionService.toggleQuestionStatus("host@example.com", 43L, 10L);

        assertThat(response.getQuestionNo()).isEqualTo(10L);
        assertThat(response.getStatus()).isEqualTo("ANSWERED");
        verify(questionMapper).updateQuestionStatus(10L, QuestionStatus.ANSWERED);
    }

    @Test
    void togglesAnsweredQuestionUsingCurrentDatabaseStatus() {
        Question question = Question.builder()
                .no(10L)
                .roomNo(43L)
                .status("ANSWERED")
                .build();

        when(questionMapper.existsByRoomNoAndQuestionNo(43L, 10L)).thenReturn(1);
        when(questionMapper.selectQuestionByQuestionNo(10L)).thenReturn(question);
        when(questionMapper.updateQuestionStatus(10L, QuestionStatus.WAITING)).thenReturn(1);

        ToggleAnswerStatusResponse response =
                questionService.toggleQuestionStatus("host@example.com", 43L, 10L);

        assertThat(response.getQuestionNo()).isEqualTo(10L);
        assertThat(response.getStatus()).isEqualTo("WAITING");
        verify(questionMapper).updateQuestionStatus(10L, QuestionStatus.WAITING);
    }
}
