package com.malmoim.service.impl;

import com.malmoim.mapper.QuestionMapper;
import com.malmoim.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {


    private final QuestionMapper questionMapper;

    @Override
    public void registerQuestion(long roomNo, String question) {
        questionMapper.registerQuestion(roomNo,question);

    }
}
