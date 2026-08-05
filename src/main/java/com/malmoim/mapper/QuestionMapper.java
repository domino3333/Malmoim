package com.malmoim.mapper;


import com.malmoim.domain.Question;
import com.malmoim.domain.Room;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface QuestionMapper {

    void registerQuestion(Question question);
}
