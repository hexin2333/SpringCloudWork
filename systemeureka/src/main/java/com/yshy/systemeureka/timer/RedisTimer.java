package com.yshy.systemeureka.timer;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

/**
 * @author 何欣
 * @date 2019/2/1 12:53
 */
@Slf4j
@Component
public class RedisTimer {


    private SimpleDateFormat date= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${RedisTimer.deleteTime}")
    private Long deleteTime;


    @Value("${RedisTimer.stateTime}")
    private Long stateTime;




    /**
     * 每1秒执行一次
     */
    @Async
    @Scheduled(cron = "0/1 * * * * *")
    public void scheduledMonitorClient() throws ParseException {
        long newDateLong = System.currentTimeMillis();
        SimpleDateFormat queryTime= new SimpleDateFormat("yyyyMMdd");
        Set<String> keys = redisTemplate.keys(queryTime.format(new Date())+"_*");
        for (String key :keys) {
                Set<Object> fieldKeys = redisTemplate.opsForHash().keys(key);
                List list = new ArrayList<>(fieldKeys);
                Collections.reverse(list);
                String o = (String)list.get(0);
                Date redisDate = date.parse(o);
                long redisTime = redisDate.getTime();
                long gapTime =newDateLong/1000 - redisTime/1000;
                if(gapTime>=stateTime){
                    redisTemplate.opsForValue().set("state_"+key.split("_")[1],"false");
                }
        }

    }

    @Async
    @Scheduled(cron = "0 5 14 * * *")
    public void scheduledMonitorRedis() {
        long newDateLong = System.currentTimeMillis();
        long gapTime = newDateLong - (deleteTime * 86400000);
        SimpleDateFormat queryTime= new SimpleDateFormat("yyyyMMdd");
        String format = queryTime.format(new Date(gapTime));
        log.info("清理时间为："+format);
        log.info("开始————定时清理过期心跳，当前时间：" + date.format(new Date()));
        Set<String> keys = redisTemplate.keys(format+"_*");
        for (String key :keys) {
            redisTemplate.delete(key);
        }
        log.info("结束————定时清理过期心跳，当前时间：" + date.format(new Date()));
    }

}
