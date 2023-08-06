//-*- coding =utf-8 -*-
//@Time : 2023/7/5
//@Author: 邓闽川
//@File  MessageQueueController.java
//@software:IntelliJ IDEA
package me.deve.streamq.broker;

import lombok.Getter;
import me.deve.streamq.common.message.Message;
import me.deve.streamq.common.message.MessageInfo;
import me.deve.streamq.common.message.MessageQueue;
import me.deve.streamq.common.message.MessageQueueInfo;
import me.devedmc.streamq.commitlog.CommitLog;

public class MessageQueueController {
    private final MessageQueue messageQueue=new MessageQueue();

    @Getter
    private CommitLog commitLog=CommitLog.getInstance();

    public  MessageQueueController(){

    }

    /**
     * 添加到索引队列中
     * 持久化到磁盘
     * @param message
     * @return
     */
    public Long add(Message message) {
        Long offset;
        synchronized (this) {
            offset=commitLog.add(message);
            MessageInfo messageInfo = new MessageInfo(offset, (long) CommitLog.getLength(message));
            messageQueue.add(messageInfo);
        }
        return offset ;
    }
    public void setMessageQueueInfo(MessageQueueInfo messageQueueInfo){
        this.messageQueue.setMessageQueueInfo(messageQueueInfo);
    }


}
