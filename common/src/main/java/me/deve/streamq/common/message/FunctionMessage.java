//-*- coding =utf-8 -*-
//@Time : 2023/6/19
//@Author: 邓闽川
//@File  FunctionMessage.java
//@software:IntelliJ IDEA
package me.deve.streamq.common.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.deve.streamq.common.component.Broker;
import me.deve.streamq.common.util.serializer.Serializer;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FunctionMessage implements Serializable {
    private FunctionMessageType messageType;

    private Broker broker;

    private Message message;

    private List<String> topics;

    private Long offset=0L;


    public FunctionMessage(FunctionMessageType functionMessageType,List<String> topics) {
        this.messageType=functionMessageType;
        this.topics=topics;
    }
    public FunctionMessage(FunctionMessageType functionMessageType) {
        this.messageType=functionMessageType;

    }

    public FunctionMessage(FunctionMessageType functionMessageType,Message message){
        this.messageType=functionMessageType;
        this.message=message;
    }
    public FunctionMessage(FunctionMessageType functionMessageType,Broker broker){
        this.messageType=functionMessageType;
        this.broker=broker;
    }

}
