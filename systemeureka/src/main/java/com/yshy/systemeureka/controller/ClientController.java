package com.yshy.systemeureka.controller;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 何欣
 * @date 2019/2/1 11:05
 */
@Slf4j
@RestController
@RequestMapping("ClientHeartbeat/")
public class ClientController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**请求参数中的客户端唯一标识Key*/
    @Value("${clientRequest.clientIdMapKey}")
    private String clientIdMapKey;

    private SimpleDateFormat dateKey= new SimpleDateFormat("yyyyMMdd");

    /**
     * 客户端心跳控制器
     * @param request
     * @return true：添加value正常 false：添加value错误
     */
    @RequestMapping("RequestHeartbeat")
    public boolean test(HttpServletRequest request){

        Map requestParamMap = getRequestParamMap(request);
        String clientId = (String)requestParamMap.get(clientIdMapKey);
        String value = JSON.toJSONString(requestParamMap);
        if(redisTemplate.opsForValue().get("state_"+clientId)==null || "false".equals(redisTemplate.opsForValue().get("state_"+clientId))){
            redisTemplate.opsForValue().set("state_"+clientId,"true");
        }
        //在redis中使用Hash来储存客户端的心跳信息 clientId：客户端唯一表示字段 field：接受心跳时间字符串 value：客户端心跳信息JSON字符串
        SimpleDateFormat date= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date newTime = new Date();
        String field =date.format(newTime);
        redisTemplate.opsForHash().put(dateKey.format(newTime)+"_"+clientId,field,value);
        Object o = redisTemplate.opsForHash().get(dateKey.format(newTime)+"_"+clientId, field);
        return o.equals(value);
    }


    @RequestMapping("test")
    public Map test(){
        SimpleDateFormat date= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<Object, Object> entries = redisTemplate.opsForHash().entries("20190202_myte");
        return  entries;
    }

    /**
     * 将request中的参数转换为map集合
     * @param request 请求对象
     * @return 请求信息map集合
     */
    public  Map getRequestParamMap(HttpServletRequest request){
        Map map = new HashMap();
        //得到枚举类型的参数名称，参数名称若有重复的只能得到第一个
        Enumeration enums = request.getParameterNames();
        while (enums.hasMoreElements())
        {
            String paramName = (String) enums.nextElement();
            String paramValue = request.getParameter(paramName);

            //形成键值对应的map
            map.put(paramName, paramValue);
        }
        return map;
    }

}
