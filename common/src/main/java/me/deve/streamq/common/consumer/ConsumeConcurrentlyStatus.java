//-*- coding =utf-8 -*-
//@Time : 2023/7/24
//@Author: 邓闽川
//@File  ConsumeConcurrentlyStatus.java
//@software:IntelliJ IDEA
package me.deve.streamq.common.consumer;

public enum ConsumeConcurrentlyStatus {
    /**
     * Success consumption
     */
    CONSUME_SUCCESS,
    /**
     * Failure consumption,later try to consume
     */
    RECONSUME_LATER;
}
