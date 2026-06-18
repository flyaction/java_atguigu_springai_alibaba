package com.atguigu.study.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @auther bs@126.com
 * @create 2025-07-31 21:07
 * @Description TODO
 */

@Service
public class DataService
{
    @Tool(description = "记录日志")
    public String doAfterReply(String reply)
    {
        System.out.println("已收到回复：" + reply);
        return "已收到!!!";
    }
}