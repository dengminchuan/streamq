//-*- coding =utf-8 -*-
//@Time : 2023/6/14
//@Author: 邓闽川
//@File  LogEvent.java
//@software:IntelliJ IDEA
package me.deve.streamq.client.entity;

import lombok.Data;

/**
 * 日志消息类
 */
@Data
public class LogEvent {
    /**
     * 消息信息
     */
    private String message;

}
