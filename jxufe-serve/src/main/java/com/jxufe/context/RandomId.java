package com.jxufe.context;


import cn.hutool.core.util.RandomUtil;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class RandomId {

    public static Long getId(){
        // 获取当前东八区的时间
        ZonedDateTime nowInEast8 = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));

        // 将东八区的时间转换为Instant对象
        Instant instantNow = nowInEast8.toInstant();

        // 将Instant对象转换为毫秒数（时间戳）
        long timestamp = instantNow.toEpochMilli();

        long substring = Long.parseLong(String.valueOf(timestamp).substring(2)) ;

        long number = RandomUtil.randomLong(100000,999999);
        return substring << 6 | number;
    }

}
