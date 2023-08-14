//-*- coding =utf-8 -*-
//@Time : 2023/7/19
//@Author: 邓闽川
//@File  FurySerializer.java
//@software:IntelliJ IDEA
package me.deve.streamq.common.util.serializer;

import io.fury.Fury;
import io.fury.Language;
import io.fury.ThreadSafeFury;
import me.deve.streamq.common.message.FunctionMessage;
import me.deve.streamq.common.message.FunctionMessageType;
import me.deve.streamq.common.message.Message;
import me.deve.streamq.common.message.MessageType;


public class FurySerializer implements Serializer{

    private Fury fury= Fury.builder()
            .withLanguage(Language.JAVA)
            .requireClassRegistration(true)
            .build();
    public FurySerializer(){
        fury.register(Message.class);
        fury.register(MessageType.class);
        fury.register(FunctionMessage.class);
        fury.register(FunctionMessageType.class);
    }

    @Override
    public byte[] serialize(Object object) {
       return fury.serialize(object);
    }

    @Override
    public <T> T deserialize(byte[] byteArray, Class<T> clazz) {
        T deserialize = (T) fury.deserialize(byteArray);
        return (T) deserialize;
    }
}
