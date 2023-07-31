//-*- coding =utf-8 -*-
//@Time : 2023/6/20
//@Author: 邓闽川
//@File  KryoSerializer.java
//@software:IntelliJ IDEA
package me.deve.streamq.common.util.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import me.deve.streamq.common.component.Broker;
import me.deve.streamq.common.component.Topic;
import me.deve.streamq.common.message.FunctionMessage;
import me.deve.streamq.common.message.FunctionMessageType;
import me.deve.streamq.common.address.KryoInetAddress;
import me.deve.streamq.common.message.Message;
import me.deve.streamq.common.message.MessageType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Supplier;

public class KryoSerializer implements  Serializer{

    /**
     * return a no-registry kryo object
     */
    private Kryo kryo;
    private final ThreadLocal<Kryo> kryoThreadLocal=ThreadLocal.withInitial(new Supplier<Kryo>() {
        @Override
        public Kryo get() {
            kryo = new Kryo();
            kryo.setRegistrationRequired(false);
            kryo.setReferences(true);
            registerAllClass(kryo);
            return kryo;
        }
    });


    @Override
    public byte[] serialize(Object object) {
        try (
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             Output output = new Output(byteArrayOutputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output,object);
            kryoThreadLocal.remove();
            return  output.toBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T deserialize(byte[] byteArray, Class<T> clazz) {
        try (   
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
                Input input = new Input(byteArrayInputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            kryoThreadLocal.remove();
            Object o = kryo.readObject(input, clazz);
            return clazz.cast(o);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void registerAllClass(Kryo kryo){
            kryo.register(FunctionMessage.class);
            kryo.register(KryoInetAddress.class);
            kryo.register(FunctionMessageType.class);
            kryo.register(Broker.class);
            kryo.register(Topic.class);
            kryo.register(java.util.HashMap.class);
            kryo.register(java.util.ArrayList.class);
            kryo.register(Message.class);
            kryo.register(MessageType.class);
            kryo.register(byte[].class);
            kryo.register(java.lang.String[].class);
            kryo.register(java.lang.String[].class);
    }
}
