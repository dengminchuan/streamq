//-*- coding =utf-8 -*-
//@Time : 2023/6/26
//@Author: 邓闽川
//@File  MessageQueue.java
//@software:IntelliJ IDEA
package me.deve.streamq.common.message;

import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.deve.streamq.common.constant.MessageConstant;
import me.deve.streamq.common.queue.ProcessQueue;
import me.deve.streamq.common.thread.ShutdownHookThread;
import me.deve.streamq.common.util.FileUtil;
import me.deve.streamq.common.util.serializer.FurySerializer;
import me.deve.streamq.common.util.serializer.KryoSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 */
@Slf4j
public class MessageQueue {
    private String location;
    private static final String MESSAGE_QUEUE_PATH=System.getProperty("file.separator")+"/messageQueue.log";
    @Getter
    private ProcessQueue processQueue;

    public void setMessageQueueInfo(MessageQueueInfo messageQueueInfo) {
        this.messageQueueInfo = messageQueueInfo;
    }
    private MessageQueueInfo messageQueueInfo;
    private FurySerializer furySerializer = new FurySerializer();
    public MessageQueue(){
        location=System.getProperty("user.dir")+ MESSAGE_QUEUE_PATH;
        Runtime.getRuntime().addShutdownHook(new ShutdownHookThread(() -> {
            byte[] serializeBytes = furySerializer.serialize(this.processQueue);
            //record current file index
            File file = new File(location);
            FileUtil.write2Binary(file,serializeBytes,false);
            return null;
        }));
        File file = new File(location);
        if(file.exists()){
            try (FileInputStream fis = new FileInputStream(file)){
                processQueue = furySerializer.deserialize(fis.readAllBytes(), ProcessQueue.class);
                log.info("load process queue,queue size:{}",processQueue.getConsumeQueue().size());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            processQueue=new ProcessQueue();
            log.info("create new queue");
        }
        messageQueueInfo=new MessageQueueInfo();
    }
    public Long add(MessageInfo messageInfo){
        return processQueue.add(messageInfo);
    }
    public MessageInfo readMessage(Long offset){
           return processQueue.readMessage(offset);
    }



}
