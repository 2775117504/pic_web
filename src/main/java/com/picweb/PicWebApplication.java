package com.picweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync //启动类上添加 @EnableAsync 注解，以开启对异步方法的支持
@SpringBootApplication
public class PicWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(PicWebApplication.class, args);
        System.out.println("启动成功");
        System.out.println("http://localhost:8080");
    }

}
