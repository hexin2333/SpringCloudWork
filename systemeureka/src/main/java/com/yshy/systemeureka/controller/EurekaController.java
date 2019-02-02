package com.yshy.systemeureka.controller;

import ch.qos.logback.core.net.server.Client;
import com.yshy.systemeureka.bean.ClientBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 何欣
 * @date 2019/2/1 15:06
 */
@Controller
public class EurekaController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private SimpleDateFormat queryTime= new SimpleDateFormat("yyyyMMdd");

    private SimpleDateFormat date= new SimpleDateFormat("yyyy-MM-dd");

    @RequestMapping("getClientMessage")
    @ResponseBody
    public List<ClientBean> getClientMessage(ClientBean clientBean) throws ParseException {
        List<ClientBean> list = new ArrayList<>();
        if(isNull(clientBean.getClientId())){
            Date parse = new Date();
            if(isNull(clientBean.getHeartbeatTime())){
                parse = date.parse(clientBean.getHeartbeatTime());
            }
            String format = queryTime.format(parse);
            Map<Object, Object> entries = redisTemplate.opsForHash().entries( format+"_"+clientBean.getClientId());
            Set<Object> objects = entries.keySet();
            for (Object object:objects) {
                ClientBean clientBeanList = new ClientBean();
                clientBeanList.setClientId(clientBean.getClientId());
                clientBeanList.setHeartbeatTime(object.toString());
                clientBeanList.setJsonMap(entries.get(object).toString());
                clientBeanList.setState(redisTemplate.opsForValue().get("state_"+clientBean.getClientId()));
                list.add(clientBeanList);
            }
        }
        return list;
    }

    private boolean isNull(String str){
        if(str==null||"".equals(str)){
            return false;
        }
        return true;
    }
}
