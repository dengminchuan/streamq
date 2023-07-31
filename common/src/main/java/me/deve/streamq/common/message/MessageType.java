//-*- coding =utf-8 -*-
//@Time : 2023/6/15
//@Author: 邓闽川
//@File  MessageType.java
//@software:IntelliJ IDEA
package me.deve.streamq.common.message;

/**
 * 消息类型
 */
public enum MessageType {
    /**
     * 同步普通消息
     */
    SynchronousNORMAL,
    /**
     * 同步异步消息
     */
    AsynchronousNormal,
    /**
     * 单向消息
     */
    ONEWAY

}
