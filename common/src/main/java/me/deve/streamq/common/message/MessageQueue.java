//-*- coding =utf-8 -*-
//@Time : 2023/6/26
//@Author: 邓闽川
//@File  MessageQueue.java
//@software:IntelliJ IDEA
package me.deve.streamq.common.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.deve.streamq.common.queue.ProcessQueue;
@Data
@AllArgsConstructor
/**
 *
 */
public class MessageQueue {
    private ProcessQueue processQueue;

    private MessageQueueInfo messageQueueInfo;



    public MessageQueue(){
        processQueue=new ProcessQueue();
        messageQueueInfo=new MessageQueueInfo();
    }
    public void add(Message message){

    }

}
