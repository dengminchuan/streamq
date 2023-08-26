//-*- coding =utf-8 -*-
//@Time : 2023/6/29
//@Author: 邓闽川
//@File  SendMsgStrategyEnum.java
//@software:IntelliJ IDEA
package me.deve.streamq.client.msgstrategy;

/**
 * broker choose strategy when send message
 */
public enum BrokerChooseStrategy {
    ROUND_ROBIN,
    RANDOM,
    HASH,
    CONSISTENCY_HASH

}
