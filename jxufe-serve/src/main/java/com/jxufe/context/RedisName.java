package com.jxufe.context;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

//Todo
public class RedisName {

    public static String getName(){

        // 获取当前东八区的时间
        ZonedDateTime nowInEast8 = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));

        // 将东八区的时间转换为Instant对象
        Instant instantNow = nowInEast8.toInstant();

        // 将Instant对象转换为毫秒数（时间戳）
        long timestamp = instantNow.toEpochMilli();

        String substring = String.valueOf(timestamp).substring(6) ;

        String name = "Lock:" + BaseContext.getCurrentId().toString() +substring;


        return null;
    }
}
