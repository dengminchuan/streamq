//-*- coding =utf-8 -*-
//@Time : 2023/7/3
//@Author: 邓闽川
//@File  ProcessQueue.java
//@software:IntelliJ IDEA
package me.deve.streamq.common.queue;

import me.deve.streamq.common.message.Message;
import me.deve.streamq.common.message.MessageExtBatch;
import me.deve.streamq.common.message.MessageInfo;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * store message
 */
public class ProcessQueue {



    private ConcurrentLinkedQueue<MessageInfo> consumeQueue;




    public ProcessQueue(){
        consumeQueue=new ConcurrentLinkedQueue<>();
    }

    public void  add(){

    }


}
