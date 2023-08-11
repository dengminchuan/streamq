//-*- coding =utf-8 -*-
//@Time : 2023/7/3
//@Author: 邓闽川
//@File  ProcessQueue.java
//@software:IntelliJ IDEA
package me.deve.streamq.common.queue;

import lombok.Getter;
import me.deve.streamq.common.message.Message;
import me.deve.streamq.common.message.MessageInfo;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * store message
 */
public class ProcessQueue {

    @Getter
    private CopyOnWriteArrayList<byte[]> consumeQueue=new CopyOnWriteArrayList<>();
    public ProcessQueue(){

    }

    public Long add(MessageInfo messageInfo){
        consumeQueue.add(messageInfo.toByteArray());
        return (long) (consumeQueue.size() - 1);
    }

    public MessageInfo readMessage(Long offset){
        return MessageInfo.fromByteArray(consumeQueue.get(Math.toIntExact(offset)));
    }



}
