//-*- coding =utf-8 -*-
//@Time : 2023/7/5
//@Author: 邓闽川
//@File  MessageQueueController.java
//@software:IntelliJ IDEA
package me.deve.streamq.broker;

import me.deve.streamq.common.message.Message;
import me.deve.streamq.common.message.MessageQueue;
import me.devedmc.streamq.commitlog.CommitLog;

public class MessageQueueController {
    private final MessageQueue messageQueue=new MessageQueue();

    private CommitLog commitLog=CommitLog.getInstance();

    public  MessageQueueController(){

    }

    /**
     * 添加到索引队列中
     * 持久化到磁盘
     * @param message
     * @return
     */
    public  boolean add(Message message) {
        boolean isAdd1;
        boolean isAdd2;
        synchronized (this) {
            isAdd1 = messageQueue.add(message);
            isAdd2=commitLog.add(message)==-1;
        }

        return isAdd1 &&isAdd2 ;
    }

}
