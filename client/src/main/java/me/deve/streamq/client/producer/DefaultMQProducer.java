//-*- coding =utf-8 -*-
//@Time : 2023/6/15
//@Author: 邓闽川
//@File  Producer.java
//@software:IntelliJ IDEA
package me.deve.streamq.client.producer;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import me.deve.streamq.client.handler.ClientHandler;
import me.deve.streamq.client.handler.ProduceHandler;
import me.deve.streamq.common.message.Message;
import me.deve.streamq.client.msgstrategy.BrokerChooseStrategy;
import me.deve.streamq.common.address.KryoInetAddress;
import me.deve.streamq.common.component.Broker;
import me.deve.streamq.common.config.NettyClientConfig;
import me.deve.streamq.remoting.netty.NettyClient;
import me.deve.streamq.remoting.thread.NettyClientThread;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static me.deve.streamq.client.util.ClientUtil.chooseBroker;

/**
 * 默认生产者
 */
@Slf4j
public class DefaultMQProducer implements MQProducer{
    /**
     * 消息所在组
      */
    private String groupName;
    /**
     * mq服务器地址
     */
    private KryoInetAddress inetAddress;

    private NettyClient serverFindClient;

    private NettyClient messageSendClient;

    private final NettyClientConfig serverFindClientConfig;

    private NettyClientConfig messageSendClientConfig;

    private final ClientHandler clientHandler=new ClientHandler();

    private final ProduceHandler produceHandler=new ProduceHandler();

    public static HashMap<String, List<Broker>> topicRouteData;

    private final ReentrantLock lock=new ReentrantLock();

    private Condition condition;

    private final Semaphore semaphore=new Semaphore(0);

    private BrokerChooseStrategy strategy= BrokerChooseStrategy.RANDOM;


    private static final ThreadPoolExecutor threadPool=new ThreadPoolExecutor(
            20,
            100,
            1000,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    public DefaultMQProducer(NettyClientConfig nettyClientConfig){
        this.serverFindClientConfig =nettyClientConfig;
        this.clientHandler.setSemaphore(semaphore);
        condition = lock.newCondition();
    }

    /**
     * obtain broker
     */
    @Override
    public void start() {
        createAndStartClient(serverFindClient,serverFindClientConfig,clientHandler);
        try {
            //wait clientHandler success
            semaphore.acquire();
        } catch (InterruptedException e) {
            log.error("obtain semaphore failed,msg:"+e.getMessage());
        }

    }



    private void createAndStartClient(NettyClient nettyClient, NettyClientConfig clientConfig, ChannelInboundHandlerAdapter handler) {
        nettyClient = new NettyClient(new NioEventLoopGroup(),new Bootstrap(), clientConfig,handler);
        NettyClientThread nettyClientThread=new NettyClientThread(nettyClient);
        nettyClientThread.start();
    }

    /**
     * 停止producer
     */
    public void shutdown(){
        serverFindClient.shutdown();
        messageSendClient.shutdown();
    }

    /**
     *
     * @param message
     * @return SendResult
     */
    public SendResult send(final Message message){
        //todo :choose broker by topic
        KryoInetAddress brokerAddress = chooseBroker(strategy,topicRouteData,message.getTopic());
        if(brokerAddress==null){
            //todo:obtain address twice
        }
        messageSendClientConfig=new NettyClientConfig(brokerAddress);
        createAndStartClient(messageSendClient,messageSendClientConfig,produceHandler);
        while(!produceHandler.getConnected()){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        produceHandler.sendMsg(message);
        return null;
    }



    @Override
    public void setMsgStrategy(BrokerChooseStrategy strategy) {
        this.strategy=strategy;
    }

}
