package me.deve.streamq.client;

import me.deve.streamq.client.consumer.DefaultMQPushConsumer;
import me.deve.streamq.common.consumer.ConsumeConcurrentlyStatus;
import me.deve.streamq.common.message.Message;
import me.deve.streamq.client.producer.DefaultMQProducer;
import me.deve.streamq.common.address.KryoInetAddress;
import me.deve.streamq.common.config.NettyClientConfig;
import me.deve.streamq.common.message.MessageExt;
import me.deve.streamq.common.message.MessageListenerConcurrently;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ClientApplication {

    public static void main(String[] args) throws InterruptedException {
        DefaultMQProducer defaultMQProducer = new DefaultMQProducer(new NettyClientConfig(new KryoInetAddress("127.0.0.1", 10088)));
        defaultMQProducer.setDefaultTopic("test topic");
        defaultMQProducer.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Timer timer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        defaultMQProducer.continuousSend(new Message("test topic",(String.valueOf(System.currentTimeMillis())).getBytes(),new String[]{"test"}));

                    }
                };
                timer.schedule(timerTask,0,100);

            }
        }).start();
        DefaultMQPushConsumer defaultMQConsumer = new DefaultMQPushConsumer(new NettyClientConfig(new KryoInetAddress("127.0.0.1", 10088)));
        defaultMQConsumer.subscribe("test topic");
        defaultMQConsumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(Message message) {
                System.out.println(new String(message.getBody()));
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        defaultMQConsumer.start();
    }

}
