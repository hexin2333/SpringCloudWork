package com.yshy.systemeureka.bean;

import lombok.Data;

/**
 * @author 何欣
 * @date 2019/2/2 14:15
 */
@Data
public class ClientBean {

    private String clientId;

    private String heartbeatTime;

    private String jsonMap;

    private String state;
}
