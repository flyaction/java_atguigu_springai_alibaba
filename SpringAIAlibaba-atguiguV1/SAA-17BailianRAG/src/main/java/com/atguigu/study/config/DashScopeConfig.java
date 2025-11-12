package com.atguigu.study.config;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DashScopeConfig
{

    @Value("${spring.ai.dashscope.api-key}")
    private String apiKey;

    @Bean
    public DashScopeApi dashScopeApi() {
        return DashScopeApi.builder()
                .apiKey(apiKey)
                //.workSpaceId("llm-3as714s6flm80yc1")//业务空间id,需要修改成自己的,不填，取默认的
                .build();
    }

    @Bean
    public ChatClient chatClient(ChatModel dashscopeChatModel) {
        return ChatClient.builder(dashscopeChatModel).build();
    }
}
