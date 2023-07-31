package me.deve.streamq.client;

import me.deve.streamq.client.consumer.DefaultMQPushConsumer;
import me.deve.streamq.common.consumer.ConsumeConcurrentlyStatus;
import me.deve.streamq.common.message.Message;
import me.deve.streamq.client.producer.DefaultMQProducer;
import me.deve.streamq.common.address.KryoInetAddress;
import me.deve.streamq.common.config.NettyClientConfig;
import me.deve.streamq.common.message.MessageExt;
import me.deve.streamq.common.message.MessageListenerConcurrently;

import java.util.List;

public class ClientApplication {

    public static void main(String[] args) {
        DefaultMQPushConsumer defaultMQConsumer = new DefaultMQPushConsumer(new NettyClientConfig(new KryoInetAddress("127.0.0.1", 10088)));
        defaultMQConsumer.subscribe("test topic");
        defaultMQConsumer.registerMessageListener(message -> null);
        defaultMQConsumer.start();
        DefaultMQProducer defaultMQProducer = new DefaultMQProducer(new NettyClientConfig(new KryoInetAddress("127.0.0.1", 10088)));
        defaultMQProducer.start();
        defaultMQProducer.send(new Message("test topic","test".getBytes()));



    }

}
