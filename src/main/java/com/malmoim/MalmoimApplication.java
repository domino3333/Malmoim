package com.malmoim;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.malmoim.mapper")
public class MalmoimApplication {

    public static void main(String[] args) {
        SpringApplication.run(MalmoimApplication.class, args);
    }

}
