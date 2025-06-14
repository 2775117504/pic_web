package com.picweb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration //配置类
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 自定义本地资源映射
        registry.addResourceHandler("/img/**") //网页端的访问路经
                .addResourceLocations("file:\\E:\\")
                .addResourceLocations("file:\\C:\\")
                .addResourceLocations("file:\\D:\\")
                .addResourceLocations("file:\\F:\\");



    }
}