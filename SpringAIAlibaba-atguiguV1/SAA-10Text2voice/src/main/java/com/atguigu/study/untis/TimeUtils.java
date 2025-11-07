package com.atguigu.study.untis;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author: action
 * @create: 2025/11/7 10:28
 **/
public class TimeUtils {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public static String getTimestamp() {
        return LocalDateTime.now().format(formatter);
    }
}
