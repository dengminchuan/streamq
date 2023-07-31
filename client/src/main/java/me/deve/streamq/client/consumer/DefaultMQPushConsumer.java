//-*- coding =utf-8 -*-
//@Time : 2023/7/5
//@Author: 邓闽川
//@File  DefaultMQConsumer.java
//@software:IntelliJ IDEA
package me.deve.streamq.client.consumer;

import com.esotericsoftware.kryo.Kryo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import me.deve.streamq.client.handler.ConsumerGetBrokerHandler;
import me.deve.streamq.client.handler.ConsumerPullHandler;
import me.deve.streamq.common.address.KryoInetAddress;
import me.deve.streamq.common.component.Broker;
import me.deve.streamq.common.config.NettyClientConfig;
import me.deve.streamq.common.message.MessageListener;
import me.deve.streamq.common.message.MessageListenerConcurrently;
import me.deve.streamq.remoting.netty.NettyClient;
import me.deve.streamq.remoting.thread.NettyClientThread;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Slf4j
public class DefaultMQPushConsumer implements MQConsumer {
    private NettyClient consumerNettyClient;

    private NettyClientConfig consumerConfig;

    private ConsumerGetBrokerHandler consumerHandler=new ConsumerGetBrokerHandler();

    private HashMap<String,List<Broker>> consumerTargetBrokers;


    private MessageListenerConcurrently messageListener=null;


    private ThreadPoolExecutor threadPool=new ThreadPoolExecutor(
            10,
            20,
            100,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            new ThreadPoolExecutor.CallerRunsPolicy());
    public void setConsumerTargetBrokers(HashMap<String,List<Broker>> consumerTargetBrokers){
        this.consumerTargetBrokers=consumerTargetBrokers;
    }

    /**
     * consumer message
     */
    @Override
    public void registerMessageListener(MessageListenerConcurrently messageListenerConcurrently) {
        this.messageListener=messageListenerConcurrently;
    }

    public List<String> getTopics() {
        return topics;
    }

    private final List<String> topics=new ArrayList<>();
    public DefaultMQPushConsumer(NettyClientConfig consumerConfig){
        this.consumerConfig=consumerConfig;
        consumerHandler.setMqConsumer(this);
    }
    @Override
    public void start() {
        if(messageListener==null){
            throw new RuntimeException("messageListener is null");
        }
        consumerHandler.setTopics(topics);

        createAndStartClient(consumerNettyClient,consumerConfig);
        try {
            consumerHandler.getSemaphore().acquire();
        } catch (InterruptedException e) {
            log.info("establishing connection error:"+e);
        }
        connectConsumers(consumerTargetBrokers);


    }

    private void connectConsumers(HashMap<String, List<Broker>> consumerTargetBrokers) {
        ArrayList<KryoInetAddress> inetSocketAddresses = new ArrayList<>();
        consumerTargetBrokers.forEach((s, brokers) ->
                brokers.forEach(
                        broker ->{
                            inetSocketAddresses.add(broker.getInetAddress());
                        }
        ));
                threadPool.execute(() -> {
            NettyClientConfig nettyClientConfig = new NettyClientConfig();
            ConsumerPullHandler consumerPullHandler = new ConsumerPullHandler();
            consumerPullHandler.setMessageListener(messageListener);
            NettyClient nettyClient = new NettyClient(new NioEventLoopGroup(), new Bootstrap(), nettyClientConfig, consumerPullHandler);
            nettyClient.startMultiple(inetSocketAddresses);
        });


    }
    private void createAndStartClient(NettyClient nettyClient, NettyClientConfig clientConfig) {
        nettyClient = new NettyClient(new NioEventLoopGroup(),new Bootstrap(), clientConfig,consumerHandler);
        NettyClientThread nettyClientThread=new NettyClientThread(nettyClient);
        nettyClientThread.start();
    }


    @Override
    public void shutdown() {

    }

    @Override
    public void subscribe(String topic) {
        topics.add(topic);
    }

    @Override
    public void unsubscribe(String topic) {
        topics.remove(topic);
    }

    @Override
    public void setPullInterval(long pullInterval, TimeUnit timeUnit) {

    }
}
