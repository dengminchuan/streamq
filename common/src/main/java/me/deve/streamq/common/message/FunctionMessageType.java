//-*- coding =utf-8 -*-
//@Time : 2023/6/19
//@Author: 邓闽川
//@File  HeartbeatType.java
//@software:IntelliJ IDEA
package me.deve.streamq.common.message;

public enum FunctionMessageType {
    BROKER_REGISTRY,
    BROKER_UPLOAD_HEARTBEAT,
    OBTAIN_BROKERS,
    NORMAL_MESSAGE,
    CONSUMER_READY,
    PULL_MESSAGE,
    CONSUMER_LOGOUT,
    REGISTER_PULL_REQUEST

}
