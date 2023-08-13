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
//            DefaultMQPushConsumer defaultMQConsumer = new DefaultMQPushConsumer(new NettyClientConfig(new KryoInetAddress("127.0.0.1", 10088)));
//            defaultMQConsumer.subscribe("test topic");
//            defaultMQConsumer.registerMessageListener(new MessageListenerConcurrently() {
//                @Override
//                public ConsumeConcurrentlyStatus consumeMessage(Message message) {
//                    System.out.println(message);
//                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//                }
//            });
//            defaultMQConsumer.start();
        DefaultMQProducer defaultMQProducer = new DefaultMQProducer(new NettyClientConfig(new KryoInetAddress("127.0.0.1", 10088)));
        defaultMQProducer.setDefaultTopic("test topic");
        defaultMQProducer.start();
        for (int i=0;i<100;i++) {
            defaultMQProducer.continuousSend(new Message("test topic","test message".getBytes(),new String[]{"test"}));
        }
    }

}
