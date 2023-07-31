//-*- coding =utf-8 -*-
//@Time : 2023/7/3
//@Author: 邓闽川
//@File  MessageExtBatch.java
//@software:IntelliJ IDEA
package me.deve.streamq.common.message;

import lombok.extern.slf4j.Slf4j;
import me.deve.streamq.common.constant.MessageConstant;

@Slf4j
public class MessageExtBatch {
        private static final int MAX_BATCH_SIZE = MessageConstant.MAX_BATCH_SIZE;
        private final MessageExt[] batch=new MessageExt[MAX_BATCH_SIZE];

        private int takeIndex = 0;
        private int putIndex = 0;

        public Boolean putMessageExt(MessageExt message){
            if((putIndex+1)%MAX_BATCH_SIZE==takeIndex){
                log.warn("message full");
                return Boolean.FALSE;
            }
            batch[(putIndex++)%MAX_BATCH_SIZE]=message;
            return Boolean.TRUE;
        }
        public MessageExt takeMessageExt(MessageExt message){
            if(putIndex==takeIndex){
                log.warn("message empty");
                return null;
            }
            return batch[takeIndex++];
        }
        public void reset(){
            this.putIndex=0;
            this.takeIndex=0;
        }
}
