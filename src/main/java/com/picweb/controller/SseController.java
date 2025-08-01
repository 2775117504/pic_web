package com.picweb.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

/**
 *=======================================通用控制器: 通过sse来实现逐步推送消息的控制器===========================================
 */

@RestController
public class SseController {

    // 存储所有连接的客户端
    private static final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @GetMapping(value = "/uploadProgress", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamUploadProgress() {
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L); // 设置超时时间

        emitter.onCompletion(() -> {
            System.out.println("客户端断开连接");
            emitters.remove(emitter);
        });

        emitter.onTimeout(() -> {
            System.out.println("连接超时");
            emitter.complete();
            emitters.remove(emitter);
        });

        emitters.add(emitter);
        return emitter;

    }

    // 提供给 UploadController 调用的方法
    @Autowired
    private Executor taskExecutor; // 使用你定义的线程池

    private static final ObjectMapper objectMapper = new ObjectMapper();
    public void sendMessageToAllClients(Map<String, Object> message) throws JsonProcessingException {
        String jsonMessage = objectMapper.writeValueAsString(message);
        sendMessageToAllClients(jsonMessage);
    }

    public void sendMessageToAllClients(String message) {
        List<SseEmitter> deadEmitters = new ArrayList<>();
        emitters.forEach(emitter -> {
            taskExecutor.execute(() -> { // 异步发送，避免阻塞上传线程
                try {
                    emitter.send(message);
                } catch (IOException | IllegalStateException e) {
                    deadEmitters.add(emitter);
                    emitter.complete();
                }
            });
        });
        emitters.removeAll(deadEmitters);
    }
}