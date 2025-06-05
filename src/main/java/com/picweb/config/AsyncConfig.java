package com.picweb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/*
=======================================配置自定义线程池用于异步任务===========================================

Spring Boot 默认使用 SimpleAsyncTaskExecutor 来执行异步任务，但在高并发场
景下性能不佳。可以通过配置一个基于线程池的 TaskExecutor 来优化异步执行效率。
*/


@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutors") // 创建一个线程池，需手动为异步方法添加 @Async("taskExecutor")注解
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(16);         // 核心线程数
        executor.setMaxPoolSize(16);         // 最大线程数
        executor.setQueueCapacity(2000);      // 队列容量
        executor.setThreadNamePrefix("Async-Executor-");
        /*executor.setTaskDecorator(new ContextCopyingTaskDecorator()); // 可选：保留请求上下文*/
        /*executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());*/
        executor.initialize();
        return executor;
    }
}