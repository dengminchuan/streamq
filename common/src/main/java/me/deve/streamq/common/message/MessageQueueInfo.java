//-*- coding =utf-8 -*-
//@Time : 2023/7/5
//@Author: 邓闽川
//@File  MessageQueueInfo.java
//@software:IntelliJ IDEA
package me.deve.streamq.common.message;


import me.deve.streamq.common.component.Broker;

public class MessageQueueInfo {
    private String messageQueueName;

    private Integer id;

    private Broker broker;

    public MessageQueueInfo(){

    }
    public MessageQueueInfo(String messageQueueName, Integer id){
        this.id=id;
        this.messageQueueName=messageQueueName;
    }
    public String getMessageQueueName() {
        return messageQueueName;
    }

    public void setMessageQueueName(String messageQueueName) {
        this.messageQueueName = messageQueueName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }



}
