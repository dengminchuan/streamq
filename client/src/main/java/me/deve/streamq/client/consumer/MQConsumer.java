//-*- coding =utf-8 -*-
//@Time : 2023/7/5
//@Author: 邓闽川
//@File  MQConsumer.java
//@software:IntelliJ IDEA
package me.deve.streamq.client.consumer;

import me.deve.streamq.common.component.Broker;
import me.deve.streamq.common.message.MessageListenerConcurrently;
import org.springframework.data.redis.connection.MessageListener;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public interface MQConsumer {

    void start();
    void shutdown();

    void subscribe(String topic);
    void unsubscribe(String topic);

    void setPullInterval(long pullInterval, TimeUnit timeUnit);
    void setConsumerTargetBrokers(HashMap<String, List<Broker>> consumerTargetBrokers);

    void registerMessageListener(MessageListenerConcurrently messageListener);

}
