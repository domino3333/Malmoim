package com.malmoim.service.qna.impl;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class VoteServiceImplIntegrationTest {

    //근데 VoteServiceImplTest 여기서도 @SpringBootTest를 했으면 더 편하게 할수있던거 아닌가?
    //매퍼는 굳이 autowired로 할필요없어서 그런가?
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("테스트 DB에 연결한다")
    void connectsToTestDatabase(){
        String database = jdbcTemplate.queryForObject("SELECT DATABASE()",String.class);
        assertEquals("malmoim_test",database);
    }

}
