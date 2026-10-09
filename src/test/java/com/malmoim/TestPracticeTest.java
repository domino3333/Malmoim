package com.malmoim;

import com.malmoim.mapper.QnaRoomMapper;
import com.malmoim.mapper.QuestionMapper;
import com.malmoim.mapper.VoteMapper;
import com.malmoim.service.qna.impl.VoteServiceImpl;
import org.apache.coyote.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class TestPracticeTest {


    //근데 그냥 임의의 값을 내가 지정해서 그걸 돌려줘라고하는거면 이런 mock은 왜필요한거지? 그냥 0을 넣으면 되잖아


    @Test
    @DisplayName("없는 질문을 추천하면 거절한다")
    void requestsMissingQuestion(){

        QuestionMapper questionMapper = mock(QuestionMapper.class);
        when(questionMapper.existsByRoomNoAndQuestionNo(10L,43L)).thenReturn(0);

        VoteMapper voteMapper = mock(VoteMapper.class);
        QnaRoomMapper qnaRoomMapper = mock(QnaRoomMapper.class);

        VoteServiceImpl voteService = new VoteServiceImpl(voteMapper,qnaRoomMapper,questionMapper);

        ResponseStatusException error = assertThrows(ResponseStatusException.class,()->voteService.castVote(43L,10L,10L));

        assertEquals(HttpStatus.NOT_FOUND,error.getStatusCode());

        verifyNoInteractions(voteMapper);
    }
}
