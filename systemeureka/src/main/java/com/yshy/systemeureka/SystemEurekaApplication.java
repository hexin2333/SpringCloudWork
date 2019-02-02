package com.yshy.systemeureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SystemEurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SystemEurekaApplication.class, args);
    }

}

