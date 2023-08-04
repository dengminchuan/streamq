//-*- coding =utf-8 -*-
//@Time : 2023/7/24
//@Author: 邓闽川
//@File  MessageListenerConcurrently.java
//@software:IntelliJ IDEA
package me.deve.streamq.common.message;

import me.deve.streamq.common.consumer.ConsumeConcurrentlyStatus;

import java.util.List;

public interface MessageListenerConcurrently extends MessageListener{
    ConsumeConcurrentlyStatus consumeMessage(final Message message);
}
