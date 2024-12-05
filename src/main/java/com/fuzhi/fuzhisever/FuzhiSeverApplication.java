package com.fuzhi.fuzhisever;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FuzhiSeverApplication {

    public static void main(String[] args) {
        SpringApplication.run(FuzhiSeverApplication.class, args);
    }

}
