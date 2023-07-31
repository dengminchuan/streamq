//-*- coding =utf-8 -*-
//@Time : 2023/6/26
//@Author: 邓闽川
//@File  MessageQueueSelector.java
//@software:IntelliJ IDEA
package me.deve.streamq.client.message;

import me.deve.streamq.common.message.Message;
import me.deve.streamq.common.message.MessageQueue;

import java.util.List;

/**
 * implements this interface to choose queue
 */

public interface MessageQueueSelector {
    MessageQueue messageQueueSelect(List<MessageQueue> l, Message msg, Object arg);
}
