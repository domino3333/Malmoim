package com.malmoim;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.malmoim.mapper")
public class MalmoimApplication {

    public static void main(String[] args) {
        SpringApplication.run(MalmoimApplication.class, args);
    }

}
