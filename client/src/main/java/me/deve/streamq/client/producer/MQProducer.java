//-*- coding =utf-8 -*-
//@Time : 2023/6/16
//@Author: 邓闽川
//@File  MQProducer.java
//@software:IntelliJ IDEA
package me.deve.streamq.client.producer;


import me.deve.streamq.client.message.MessageQueueSelector;
import me.deve.streamq.common.message.Message;
import me.deve.streamq.client.msgstrategy.BrokerChooseStrategy;

public interface MQProducer {



    void start();
    void shutdown();
    SendResult send(final Message message);

    void setMsgStrategy(BrokerChooseStrategy strategy);

}
