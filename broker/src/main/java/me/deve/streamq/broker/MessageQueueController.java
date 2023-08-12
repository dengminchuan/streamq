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
import me.deve.streamq.common.util.serializer.KryoSerializer;
import me.devedmc.streamq.commitlog.CommitLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class MessageQueueController {
    private final MessageQueue messageQueue=new MessageQueue();


    private CommitLog commitLog=CommitLog.getInstance();
    @Getter
    private Long consumeOffset= (long) messageQueue.getProcessQueue().getConsumeQueue().size()-1;

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
            MessageInfo messageInfo = new MessageInfo(offset, (long) CommitLog.getLength(message),1);
            consumeOffset=offset = messageQueue.add(messageInfo);
        }
        return offset ;
    }
    public Message readMessage(Long consumerOffset){
        MessageInfo messageInfo = messageQueue.readMessage(consumerOffset);
        Long offset = messageInfo.getQueueOffset();
        Long length = messageInfo.getLength();
        return commitLog.readMessage(offset,length);

    }

    public void setMessageQueueInfo(MessageQueueInfo messageQueueInfo){
        this.messageQueue.setMessageQueueInfo(messageQueueInfo);
    }


}
