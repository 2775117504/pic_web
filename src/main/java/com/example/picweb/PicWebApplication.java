package com.example.picweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PicWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(PicWebApplication.class, args);
        System.out.println("启动成功");
    }

}
